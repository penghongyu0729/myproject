package game_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Role extends JButton{
    static public Role atkrole=null,getatkrole=null;
    private String name;
    private int hp;
    private int attack;
    private boolean ifsleep;
    private int cost;
    private int state;
    private boolean ifattacked=false;
    Role(String name,int cost,int hp,int attack)
    {
        Role temprole=this;
        this.name=name;
        this.hp=hp;
        this.attack=attack;
        this.cost=cost;
        ifsleep=true;
        setSize(61, 90);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Game_Control.playerstate!=Game_Control.nowstate){
                    System.out.println("这不是你的回合");
                    return;
                }
                if(Game_Control.gameend) {
                    System.out.println("游戏已结束");
                    return;
                }
                if(ifsleep) {
                    System.out.println("这个随从需要一个回合进行准备");
                    return;
                }
                if(ifattacked) {
                    System.out.println("这个随从已经攻击过了");
                    return;
                }
                if(state==Game_Control.nowstate){
                    if(atkrole!=null)
                        atkrole.setBorder(null);
                    atkrole=temprole;
                    setBorder(BorderFactory.createLineBorder(Color.RED));
                }
                if(state!=Game_Control.nowstate&&getatkrole==null){
                    if(atkrole==null) {
                        System.out.println("你无法操控该随从!");
                        return;
                    }
                    atkrole.ifattacked=true;
                    atkrole.do_attack(temprole);
                    atkrole.Draw();
                    temprole.Draw();
                    int atkindex=(atkrole.getX()-280)/75;
                    int getatkindex=(temprole.getX()-280)/75;
                    if(atkrole.ifdeath()){
                        Game_Control.win1.remove(atkrole);
                        Game_Control.players[(state+1)%2].getRole_list().remove(atkindex);
                    }
                    if(temprole.ifdeath()){
                        Game_Control.win1.remove(temprole);
                        Game_Control.players[state].getRole_list().remove(getatkindex);
                    }
                    String str=atkindex+" "+atkrole.name+" attack "+getatkindex+" "+temprole.name;
                    Game_Control.operation.operation_name.add(new cardString(str,atkrole.state));
                    Game_Control.players[0].Show_card(0);
                    Game_Control.players[1].Show_card(1);
                    Game_Control.win1.repaint();
                    atkrole=null;
                    Game_Control.show_ope();
                }
            }
        });
    }

    public void setIfattacked(boolean ifattacked) {
        this.ifattacked = ifattacked;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAttack() {
        return attack;
    }

    public int getHp() {
        return hp;
    }

    public String getName() {
        return name;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean ifdeath(){
        if(hp>0)
            return false;
        return true;
    }
    public void role_display(){
        System.out.println("随从名称:"+name+" attack"+attack+" hp"+hp);
    }
    public void do_attack(Role role){
        this.hp-=role.attack;
        role.hp-=this.attack;
    }
    public void wakeup(){
        ifsleep=false;
        ifattacked=false;
    }

    public boolean isIfsleep() {
        return ifsleep;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    public void Draw(){
        setBorder(BorderFactory.createLineBorder(Color.black));
        String str = "<html><body>"+name + "<br>atk:" + attack + "<br>hp:" + hp + "<br>cost:" + cost+"<body></html>";
        setText(str);
        setHorizontalAlignment(JButton.CENTER);
    }
}