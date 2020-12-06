package game_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game_Control{
    class read_time implements Runnable{
        public int state;
        @Override
        synchronized public void run() {
            state=0;
            while(!gameend){
                if(nowstate!=playerstate){
                    label1.setText("Waiting!");
                    win1.repaint();
                    try {
                        synchronized (obj) {
                            obj.wait();
                        }
                    }catch (InterruptedException e){

                    }
                }
                if(state%1000==0)
                    Game_Control.set_time(state);
                state++;
                try {
                    Thread.sleep(1);
                }catch (InterruptedException e){

                }
                if(state>9998){
                    endround();
                }
            }
        }
    }
    public Object obj=new Object();
    public Thread time_thread;
    static public boolean ifai=false;
    static public JFrame win1;
    static int nowstate;
    static public boolean gameend;
    static public Player[] players=new Player[2];
    private int round=0;
    static public int playerstate;
    private Card_Dir card_dir;
    private Player currentplayer=players[0];
    private Player waitingplayer=players[1];
    static public Operation operation=new Operation();
    static public JLabel[] ope=new JLabel[11];
    static private JLabel label1=new JLabel();
    public read_time timer=new read_time();
    static private Socket player_socket=null;
    static private DataInputStream inputStream;
    static private DataOutputStream outputStream;
    Game_Control() throws IOException {
        Random random=new Random();
        gameend=false;
        win1=new JFrame("炉石传说游戏界面");
        card_dir=new Card_Dir();
        player_socket=new Socket("192.168.47.1",8888);
        System.out.println("成功接入");
        inputStream= new DataInputStream(player_socket.getInputStream());
        outputStream=new DataOutputStream(player_socket.getOutputStream());
        String[] card=new String[2];
        System.out.println("正在匹配玩家......");
        for(int i=0;i<3;){
            String s=inputStream.readUTF();
            if(s!=null&&i==0) {
                System.out.println(s);
                playerstate=Integer.valueOf(s);
                i++;
            }
            else if(s!=null){
                System.out.println(s);
                card[i-1]=s;
                i++;
            }
        }
        nowstate=0;
        players[0]=new Player(0);
        players[1]=new Player(1);
        getcard(card);
        game_thread g_t=new game_thread();
        Thread g_thread=new Thread(g_t);
        g_thread.start();
        for(int i=0;i<11;i++){
            ope[i]=new JLabel();
            ope[i].setBounds(15,150+40*i,150,40);
            ope[i].setFont(new Font("宋体",Font.PLAIN,12));
            win1.add(ope[i]);
        }
        time_thread=new Thread(timer);
    }
    public JFrame getWin1() {
        return win1;
    }
    public void startgame(){
        //System.out.println("游戏开始!");
        if(nowstate==playerstate) {
            for (int i = 0; i < 3; i++)
                players[0].Draw_card(1);
            for (int i = 0; i < 4; i++)
                players[1].Draw_card(1);
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    synchronized static void set_time(int t_state){
        int temp_st=10-t_state/1000;
        label1.setText(temp_st+"");
        win1.repaint();
    }
    synchronized public void startround(){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        if(!time_thread.isAlive()){
            time_thread.start();
        }
        nowstate=round%2;
        if(nowstate==1) {
            System.out.println("当前回合为玩家2回合");
        }
        else {
            System.out.println("当前回合为玩家1回合");
        }
        if(playerstate==nowstate) {
            synchronized (obj) {
                obj.notifyAll();
            }
        }
        currentplayer=players[nowstate];
        currentplayer.setRestofcrystal(currentplayer.getCrystal());
        players[0].showCystal();
        players[1].showCystal();
        waitingplayer=players[(nowstate+1)%2];
        waitingplayer.add_crystal();
        players[0].Show_card(0);
        players[1].Show_card(1);
        if(nowstate!=playerstate)
            return;
        currentplayer.Draw_card(1);
        if(nowstate==1&&ifai==true){
            usingai(players[nowstate]);
        }
        win1.repaint();
    }
    synchronized public void endround(){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        timer.state=0;
        String str="Player";
        if(nowstate==0)
            str=str+"1 ";
        else
            str=str+"2 ";
        str=str+"endround";
        operation.operation_name.add(new cardString(str,nowstate));
        show_ope();
        round++;
        nowstate=(playerstate+round)%2;
        currentplayer.wakeup();
        players[0].Show_card(0);
        players[1].Show_card(1);
        players[0].ShowHeadImage();
        players[1].ShowHeadImage();
        win1.repaint();
        startround();
    }
    private void createAndShowGUI(){
        win1.setSize(1200,800);
        win1.setLocation(150,0);
        win1.setLayout(null);
        win1.setResizable(false);
        win1.setBackground(Color.DARK_GRAY);
        win1.setVisible(true);
        win1.getContentPane().setBackground(Color.YELLOW);
        win1.setDefaultCloseOperation(win1.EXIT_ON_CLOSE);
        players[0].Show_card(0);
        players[1].Show_card(1);
        players[0].ShowHeadImage();
        players[1].ShowHeadImage();
        players[0].showCystal();
        players[1].showCystal();
        JButton button=new JButton("结束回合");
        button.setBounds(1050,350,120,60);
        button.setFont(new Font("宋体",Font.PLAIN,18));
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nowstate!=playerstate)
                    return;
                endround();
            }
        });
        JButton button1=new JButton("启用AI");
        button1.setBounds(1050,280,120,60);
        button1.setFont(new Font("宋体",Font.PLAIN,18));
        button1.setFocusPainted(false);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ifai==true){
                    ifai=false;
                    button1.setText("启用AI");
                }else{
                    ifai=true;
                    button1.setText("关闭AI");
                }
            }
        });
        label1.setBounds(1050,420,120,60);
        label1.setFont(new Font("宋体",Font.PLAIN,18));
        label1.setHorizontalAlignment(JButton.CENTER);
        label1.setBorder(BorderFactory.createLineBorder(Color.RED));
        win1.add(label1);
        win1.add(button);
        win1.add(button1);
        startround();
    }
    public static void sendope(){
        if(nowstate!=playerstate)
            return;
        String Ope=operation.operation_name.get(operation.operation_name.size()-1).operation;
        try {
            outputStream.writeUTF(Ope);
            outputStream.flush();
        }catch (IOException e){

        }
    }
    static public void show_ope(){
        sendope();
        for(int j=0;j<operation.operation_name.size()&&j<11;j++){
            int i=operation.operation_name.size()-1-j;
            cardString temp_ope=operation.operation_name.get(i);
            ope[j].setText(temp_ope.operation);
            ope[j].setHorizontalAlignment(JLabel.CENTER);
            if(temp_ope.state==0){
                ope[j].setBorder(BorderFactory.createLineBorder(Color.green));
            }else{
                ope[j].setBorder(BorderFactory.createLineBorder(Color.red));
            }
        }
    }
    void usingai(Player aiplayer){
        ArrayList<Card> tempcard_list=aiplayer.getCard_list();
        while(true){
            if(aiplayer.getRole_list().size()>=7)
                break;
            int len=tempcard_list.size();
            int maxindex=-1;
            int max=-1;
            for(int i=0;i<len;i++){
                if(aiplayer.getRestofcrystal()>tempcard_list.get(i).getCard_cost()&&tempcard_list.get(i).getCard_cost()>max){
                    max=tempcard_list.get(i).getCard_cost();
                    maxindex=i;
                }
            }
            if(maxindex==-1)
                break;
            aiplayer.usingcard(maxindex,0);
        }
        if(aiplayer.getRestofcrystal()>=2) {
            aiplayer.usingcontrol();
        }
        Player temp_player=players[(nowstate+1)%2];
        for(int i=0;i<aiplayer.getRole_list().size();i++){
            if(gameend)
                break;
            Role atkrole=aiplayer.getRole_list().get(i);
            if(!atkrole.isIfsleep()) {
                temp_player.get_user().be_attacked(atkrole.getAttack());
                temp_player.ShowHeadImage();
                win1.repaint();
                temp_player.checkgame();
            }
        }
        endround();
    }
    public void getcard(String[] card){
        ArrayList<Card> card_list1=new ArrayList<>();
        String[] card1=card[0].split(" ");
        for(int i=0;i<card1.length;i++){
            int index=Integer.valueOf(card1[i]);
            card_list1.add(Card_Dir.card_dir.get(index));
        }
        players[playerstate].setRestof_card_list(card_list1);
        ArrayList<Card> card_list2=new ArrayList<>();
        String[] card2=card[1].split(" ");
        for(int i=0;i<card2.length;i++){
            int index=Integer.valueOf(card2[i]);
            card_list2.add(Card_Dir.card_dir.get(index));
        }
        players[(playerstate+1)%2].setRestof_card_list(card_list2);
    }
    void dooperation(String ope){
        System.out.println(ope);
        if(ope.contains("endround")){
            endround();
        }else if(ope.contains("Draw")){
            if(ope.contains("Player1"))
                players[0].Draw_card(1);
            else
                players[1].Draw_card(1);
        }else if(ope.contains("Use")){
            String[] str=ope.split(" ");
            if(ope.contains("Player1"))
                players[0].usingcard(Integer.valueOf(str[2]),Integer.valueOf(str[4]));
            else
                players[1].usingcard(Integer.valueOf(str[2]),Integer.valueOf(str[4]));
        }else if(ope.contains("control")){
            if(ope.contains("Player1"))
                players[0].usingcontrol();
            else
                players[1].usingcontrol();
        }else if(ope.contains("attack")){
            if(ope.contains("Player1")){
                String[] str=ope.split(" ");
                Role atkrole=players[(playerstate+1)%2].getRole_list().get(Integer.valueOf(str[0]));
                int index=Integer.valueOf(str[0]);
                atkhero(players[0],atkrole,index);
            }else if(ope.contains("Player2")){
                String[] str=ope.split(" ");
                Role atkrole=players[(playerstate+1)%2].getRole_list().get(Integer.valueOf(str[0]));
                int index=Integer.valueOf(str[0]);
                atkhero(players[1],atkrole,index);
            }else{
                String[] str=ope.split(" ");
                Role atkrole=players[(playerstate+1)%2].getRole_list().get(Integer.valueOf(str[0]));
                Role getatkrole=players[playerstate].getRole_list().get(Integer.valueOf(str[3]));
                players[(playerstate+1)%2].attackrole(atkrole,getatkrole,Integer.valueOf(str[0]),Integer.valueOf(str[3]));
            }
        }
    }
    void atkhero(Player getatkplayer,Role atkrole,int index){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        String str="";
        int atkindex=index;
        if(atkrole!=null){
            getatkplayer.get_user().be_attacked(atkrole.getAttack());
            str=atkindex+" "+atkrole.getName()+" attack "+"Player";
            atkrole.setIfattacked(true);
        }
        getatkplayer.ShowHeadImage();
        if(playerstate==0)
            str=str+"1";
        else
            str=str+"2";
        getatkplayer.checkgame();
        Game_Control.operation.operation_name.add(new cardString(str,(playerstate+1)%2));
        Game_Control.show_ope();
    }
    class game_thread implements Runnable {
        private Socket socket;
        private DataInputStream inputStream;
        game_thread(){
            socket = player_socket;
        }
        @Override
        public void run() {
            try {
                inputStream = new DataInputStream(socket.getInputStream());
                while (true) {
                    String s = inputStream.readUTF();
                    if(s!=null){
                        dooperation(s);
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
