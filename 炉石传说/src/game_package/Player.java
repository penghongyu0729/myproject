package game_package;

import javax.imageio.metadata.IIOMetadataNode;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class Player {
    private Hero user;
    private int state;
    private boolean ifcontrol=false;
    private ArrayList<Role> role_list;
    private ArrayList<Card> card_list;
    private ArrayList<Card> Restof_card_list;
    private boolean ifdeath=false;
    private int crystal=0;
    private int Restofcrystal=0;
    private ArrayList<JLabel> crystal_list=new ArrayList<>();
    private JFrame win;
    private JButton Head;
    private JButton control;
    private JLabel name;
    private JLabel rest_card;
    private int t_state;
    int dmg=0;
    Player(int state){
        t_state=Game_Control.playerstate;
        this.state=state;
        win=Game_Control.win1;
        Head=new JButton();
        rest_card=new JLabel();
        win.add(rest_card);
        Head.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Game_Control.gameend) {
                    System.out.println("游戏已结束");
                    return;
                }
                if(state==Game_Control.nowstate){
                    System.out.println("你选择了一个无效的目标");
                    return;
                }
                String str="";
                int atkindex=(Role.atkrole.getX()-280)/75;
                if(Role.atkrole!=null){
                    user.be_attacked(Role.atkrole.getAttack());
                    str=atkindex+" "+Role.atkrole.getName()+" attack "+"Player";
                    Role.atkrole.setBorder(null);
                    Role.atkrole.setIfattacked(true);
                    Role.atkrole=null;
                }
                else if(Role.atkrole==null) {
                    return;
                }
                ShowHeadImage();
                if(state==0)
                    str=str+"1";
                else
                    str=str+"2";
                checkgame();
                Game_Control.operation.operation_name.add(new cardString(str,(state+1)%2));
                Game_Control.show_ope();
            }
        });
        name=new JLabel();
        control=new JButton();
        control.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usingcontrol();
            }
        });
        win.add(Head);
        win.add(control);
        win.add(name);
        if(state==Game_Control.nowstate) {
            crystal = 1;
            Restofcrystal = 1;
        }
        role_list=new ArrayList<>();
        card_list=new ArrayList<>();
        user=new Hero(30,0);
        for(int i=0;i<10;i++){
            JLabel label=new JLabel();
            label.setVisible(false);
            crystal_list.add(label);
            win.add(label);
        }
    }

    public void setRestof_card_list(ArrayList<Card> restof_card_list) {
        Restof_card_list = restof_card_list;
    }

    public void checkgame(){
        win.repaint();
        if(user.ifdeath()){
            Game_Control.gameend=true;
            System.out.println("玩家"+(state+1)+"阵亡，游戏结束");
            System.out.println("玩家"+((state+1)%2+1)+"获得胜利!");
        }
    }
    public void ShowHeadImage(){
        String str="<html>"+"玩家"+(state+1)+"<br>hp:"+user.gethp()+"</html>";
        Head.setText(str);
        Head.setBounds(600,125+((t_state+state+1)%2)*440,75,75);
        Head.setFont(new Font("宋体",Font.PLAIN,16));
        Head.setHorizontalAlignment(JButton.CENTER);
        Head.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        name.setText("玩家"+(state+1));
        name.setBounds(70,40+630*((t_state+state+1)%2),100,50);
        name.setOpaque(true);
        name.setBackground(Color.DARK_GRAY);
        name.setFont(new Font("宋体",Font.PLAIN,18));
        name.setHorizontalAlignment(JLabel.CENTER);
        name.setForeground(Color.white);
        str="技能";
        control.setText(str);
        control.setBounds(680,135+((t_state+state+1)%2)*440,50,50);
        control.setFont(new Font("宋体",Font.PLAIN,8));
        control.setHorizontalAlignment(JButton.CENTER);
    }

    public ArrayList<Card> getCard_list() {
        return card_list;
    }

    public void Draw_card(int card_num){
        if(Restof_card_list.size()<=0){
            dmg++;
            user.be_attacked(dmg);
            System.out.println("你已经疲劳，受到"+dmg+"点伤害");
            ShowHeadImage();
            win.repaint();
            checkgame();
            return;
        }
        for(int i=0;i<card_num;i++) {
            Card temp_card = Restof_card_list.get(0);
            String str="Player";
            if(state==0)
                str=str+"1";
            else
                str=str+"2";
            str=str+" Draw "+temp_card.getCard_name();
            Game_Control.operation.operation_name.add(new cardString(str,state));
            if(card_list.size()>=10){
                System.out.print("手牌溢出，爆掉的卡牌为: ");
                temp_card.card_display();
                Restof_card_list.remove(0);
                continue;
            }
            card_list.add(new Card(temp_card.getCard_name(), temp_card.getCard_cost(), temp_card.getCard_hp(), temp_card.getCard_attack()));
            Card tempcard=card_list.get(card_list.size()-1);
            win.add(tempcard);
            tempcard.setState(state);
            Restof_card_list.remove(0);
            Show_card(state);
        }
        Game_Control.show_ope();
        win.repaint();
    }
    public boolean ifdeath(){
        if(user.ifdeath())
            return true;
        return false;
    }
    public Hero get_user(){
        return user;
    }
    public ArrayList<Role> getRole_list(){
        return role_list;
    }
    public void Show_card(int state){
        for(int i=0;i<card_list.size();i++){
            if(state==t_state){
                Card temp_card=card_list.get(i);
                card_list.get(i).Draw();
                card_list.get(i).setBounds(210+75*i,650,temp_card.getWidth(),temp_card.getHeight());
            }
            else{
                Card temp_card=card_list.get(i);
                card_list.get(i).Draw();
                card_list.get(i).setBounds(210+75*i,20,temp_card.getWidth(),temp_card.getHeight());
            }
        }
        for(int i=0;i<role_list.size();i++){
            if(state==t_state){
                Role temp_role=role_list.get(i);
                role_list.get(i).Draw();
                role_list.get(i).setBounds(280+75*i,450,temp_role.getWidth(),temp_role.getHeight());
            }
            else{
                Role temp_role=role_list.get(i);
                role_list.get(i).Draw();
                role_list.get(i).setBounds(280+75*i,220,temp_role.getWidth(),temp_role.getHeight());
            }
        }
        String str="<html>"+"玩家"+(state+1)+"<br>剩余卡牌数："+Restof_card_list.size()+"</html>";
        rest_card.setText(str);
        rest_card.setBounds(1100,125+((t_state+state+1)%2)*440,90,75);
        rest_card.setFont(new Font("宋体",Font.PLAIN,16));
        rest_card.setHorizontalAlignment(JButton.CENTER);
        rest_card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    public void add_crystal(){
        if(crystal<10)
            crystal++;
    }

    public int getRestofcrystal() {
        return Restofcrystal;
    }

    public void setRestofcrystal(int restofcrystal) {
        Restofcrystal = restofcrystal;
    }

    public int getCrystal() {
        return crystal;
    }
    public void showboard(){
        System.out.println("玩家当前生命值:"+user.gethp());
        System.out.println("玩家当前随从个数为:"+role_list.size());
        for(int i=0;i<role_list.size();i++){
            System.out.print("随从序号:"+(i+1)+" ");
           role_list.get(i).role_display();
        }
    }
    public void showCystal(){
        for(int i=0;i<crystal;i++){
            JLabel label=crystal_list.get(i);
            label.setVisible(true);
            label.setOpaque(true);
            if(i<Restofcrystal)
                label.setBackground(Color.BLUE);
            else
                label.setBackground(Color.DARK_GRAY);
            label.setBounds(950+22*i,55+630*((t_state+state+1)%2),20,20);
        }
    }
    public void wakeup(){
        ifcontrol=false;
        for(int i=0;i<role_list.size();i++) {
            role_list.get(i).wakeup();
        }
    }
    public void usingcontrol(){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        if(ifcontrol){
            System.out.println("你已经使用过这个技能");
            return;
        }
        if(Restofcrystal<2){
            System.out.println("你没有足够的法力值");
            return;
        }
        if(role_list.size()>=7){
            return;
        }
        win.add(control);
        ifcontrol=true;
        role_list.add(new Role("召唤随从",2,2,2));
        Role temprole=role_list.get(role_list.size()-1);
        temprole.setState(state);
        win.add(temprole);
        Restofcrystal-=2;
        Show_card(state);
        showCystal();
        String str="Player";
        if(state==0)
            str=str+"1";
        else
            str=str+"2";
        str=str+" control";
        Game_Control.operation.operation_name.add(new cardString(str,state));
        Game_Control.show_ope();
        win.repaint();
    }
    void usingcard(int index,int aim){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        Card tempcard=card_list.get(index);
        String str="Player";
        if(state==0)
            str=str+"1";
        else
            str=str+"2";
        str=str+" Use "+index+" "+tempcard.getCard_name()+" "+aim;
        Game_Control.operation.operation_name.add(new cardString(str,state));
        role_list.add(0,new Role(tempcard.getCard_name(),tempcard.getCard_cost(),tempcard.getCard_hp(),tempcard.getCard_attack()));
        Role temprole=role_list.get(0);
        temprole.setState(state);
        Game_Control.win1.add(temprole);
        setRestofcrystal(getRestofcrystal()-tempcard.getCard_cost());
        Game_Control.win1.remove(tempcard);
        getCard_list().remove(index);
        Show_card(state);
        showCystal();
        Game_Control.show_ope();
    }
    void attackrole(Role atkrole,Role getatkrole,int index,int aim){
        if(Game_Control.gameend) {
            System.out.println("游戏已结束");
            return;
        }
        atkrole.do_attack(getatkrole);
        int atkindex=index;
        int getatkindex=aim;
        if(atkrole.ifdeath()){
            Game_Control.win1.remove(atkrole);
            Game_Control.players[state].getRole_list().remove(atkindex);
        }
        if(getatkrole.ifdeath()){
            Game_Control.win1.remove(getatkrole);
            Game_Control.players[(state+1)%2].getRole_list().remove(getatkindex);
        }
        atkrole.setIfattacked(true);
        String str=atkindex+" "+atkrole.getName()+" attack "+getatkindex+" "+getatkrole.getName();
        Game_Control.operation.operation_name.add(new cardString(str,state));
        Game_Control.players[0].Show_card(0);
        Game_Control.players[1].Show_card(1);
        Game_Control.win1.repaint();
        Game_Control.show_ope();
    }
}
