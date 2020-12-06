package game_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Card extends JButton {
    private String card_name;
    private int card_hp;
    private int card_attack;
    private int card_cost;
    private ImageIcon icon;
    private int state;
    private int mousex,mousey;
    private Card tempcard;
    //待扩展 如种族、战吼、亡语等卡牌描述、若出现法术牌等其余牌类则Card可作为抽象类用于被继承
    //
    Card(String card_name,int card_cost,int card_hp,int card_attack){
        setFocusPainted(false);
        this.card_name=card_name;
        this.card_hp=card_hp;
        this.card_attack=card_attack;
        this.card_cost=card_cost;
        tempcard=this;
        setSize(61, 90);
        icon=new ImageIcon("src/game_package/card_background.jpg");
        icon.setImage(icon.getImage().getScaledInstance(61, 90,Image.SCALE_DEFAULT));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Game_Control.gameend) {
                    System.out.println("游戏已结束");
                    return;
                }
                if(state!=Game_Control.playerstate||state!=Game_Control.nowstate) {
                    System.out.println("这不是你的回合");
                    return;
                }
                super.mousePressed(e);
                mousex=e.getX();
                mousey=e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(Game_Control.gameend) {
                    System.out.println("游戏已结束");
                    return;
                }
                if(state!=Game_Control.playerstate||state!=Game_Control.nowstate) {
                    System.out.println("这不是你的回合");
                    return;
                }
                super.mouseReleased(e);
                int newx=e.getX()-mousex;
                int newy=e.getY()-mousey;
                newx=getX()+newx;
                newy=getY()+newy;
                if(newy<100||newx<150||newx>900||newy>490)
                    return;
                int index=0;
                for(int i=0;i<=7;i++){
                    if(newx<280+75*i){
                        index=i;
                        break;
                    }
                }
                Player player=Game_Control.players[state];
                ArrayList<Role> role_list=Game_Control.players[state].getRole_list();
                if(index>Game_Control.players[state].getRole_list().size())
                    index=Game_Control.players[state].getRole_list().size();
                if(role_list.size()==7){
                    System.out.println("你无法拥有更多随从");
                    return;
                }
                if(card_cost>player.getRestofcrystal()){
                    System.out.println("你没有足够的法力值");
                    return;
                }
                String str="Player";
                if(state==0)
                    str=str+"1";
                else
                    str=str+"2";
                int index2=(getX()-210)/75;
                str=str+" Use "+index2+" "+card_name+" "+index;
                Game_Control.operation.operation_name.add(new cardString(str,state));
                role_list.add(index,new Role(getCard_name(),getCard_cost(),getCard_hp(),getCard_attack()));
                Role temprole=role_list.get(index);
                temprole.setState(state);
                Game_Control.win1.add(temprole);
                player.setRestofcrystal(player.getRestofcrystal()-getCard_cost());
                Game_Control.win1.remove(tempcard);
                player.getCard_list().remove(index2);
                player.Show_card(state);
                player.showCystal();
                Game_Control.show_ope();
            }
        });
    }
    public void Draw(){
        if(state!=Game_Control.playerstate) {
            setIcon(icon);
            setBorder(null);
            setText(null);
        }
        else {
            setIcon(null);
            setBorder(BorderFactory.createLineBorder(Color.black));
            String str = "<html><body>"+card_name + "<br>atk:" + card_attack + "<br>hp:" + card_hp + "<br>cost:" + card_cost+"<body></html>";
            setText(str);
            setHorizontalAlignment(JButton.CENTER);
        }
        Game_Control.win1.repaint();
    }
    public int getCard_cost() {
        return card_cost;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCard_cost(int card_cost) {
        this.card_cost = card_cost;
    }

    public void setCard_attack(int card_attack) {
        this.card_attack = card_attack;
    }

    public int getCard_hp() {
        return card_hp;
    }

    public int getCard_attack() {
        return card_attack;
    }

    public void setCard_hp(int card_hp) {
        this.card_hp = card_hp;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }
    public void card_display(){
        System.out.println("卡牌名称："+card_name+" cost:"+card_cost+" attack:"+card_attack+" hp:"+card_hp);
    }
}
