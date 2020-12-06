package game_package;

public class Hero {
    private int hp;
    private int attack;
    Hero(int hp,int attack){
        this.hp=hp;
        this.attack=attack;
    }
    public int gethp(){
        return hp;
    }
    public int get_attack(){
        return attack;
    }
    public void setHp(int hp){
        this.hp=hp;
    }
    public void setAttack(int attack){
        this.attack=attack;
    }
    public boolean ifdeath(){
        if(hp>0)
            return false;
        return true;
    }
    public void be_attacked(int attack){
        hp-=attack;
    }
}
