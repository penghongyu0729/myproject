package game_package;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Card_Dir {
    static public ArrayList<Card> card_dir=new ArrayList<>();
    static private Random random;
    File file =new File("src/game_package/E.txt");
    private void init()  {
        String name="card";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            for (int i = 0; i < 200; i++) {
                String str=bufferedReader.readLine();
                String[] strs=str.split(" ");
                Card tempcard=new Card(strs[0],Integer.valueOf(strs[1]),Integer.valueOf(strs[2]),Integer.valueOf(strs[3]));
                card_dir.add(tempcard);
            }
            fileReader.close();
        }catch (IOException e){
        }
    }
    Card_Dir() {
        random=new Random();
        init();
    }
    static public ArrayList<Card> Create_Cardlist(){
        ArrayList<Card> temp_card_list=new ArrayList<>();
        for(int i=0;i<30;i++){
            int index=random.nextInt(200);
            temp_card_list.add(card_dir.get(index));
        }
        return temp_card_list;
    }
}
