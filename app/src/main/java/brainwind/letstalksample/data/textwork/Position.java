package brainwind.letstalksample.data.textwork;

import java.util.List;

public class Position
{

    /*
     This will find the position of any item in a list
     */
    public Position()
    {



    }

    public int findPos(List<String> items, String sentence)
    {

        //variable to hold the position
        int pos=-1;
        //loop through the list and find the position in the sentence
        for(int i=0;i<items.size();i++)
        {

            String item=items.get(i);
            String s=" "+sentence.trim().toLowerCase()+" ";
            int y=s.indexOf(" "+item.trim().toLowerCase()+" ");
            if(y>-1&y<pos&pos>-1||pos<0&y>-1)
            {
                pos=y;

            }

        }

        return pos;

    }

    public int getNumOfWordsOfMatch(List<String> items,String sentence)
    {

        //variable to hold the position
        int pos=-1;
        int num_words=0;
        //loop through the list and find the position in the sentence
        for(int i=0;i<items.size();i++)
        {

            String item=items.get(i).trim();
            //System.out.print("item="+item+"\n");
            String s=" "+sentence.trim().toLowerCase()+" ";
            int y=s.indexOf(" "+item.trim().toLowerCase()+" ");
            if(y>-1&y<pos&pos>-1||pos<0&y>-1)
            {
                pos=y;
                num_words=item.split(" ").length;
            }

        }

        return num_words;

    }



    public int findPos(List<String> items,String sentence,int afterpos)
    {

        //variable to hold the position
        int pos=-1;
        //loop through the list and find the position in the sentence
        for(int i=0;i<items.size();i++)
        {

            String item=items.get(i);
            String s=" "+sentence.trim().toLowerCase()+" ";
            int y=s.indexOf(" "+item.trim().toLowerCase()+" ",afterpos+1);
            if(y>-1&y<pos&pos>-1||pos<0&y>-1)
            {
                pos=y;

            }

        }

        return pos;

    }

    public int findLastPos(List<String> items,String sentence)
    {

        //variable to hold the position
        int pos=-1;

        //loop through the list and find the position in the sentence
        for(int i=0;i<items.size();i++)
        {

            String item=items.get(i);
            String s=" "+sentence.trim().toLowerCase()+" ";
            int y=s.lastIndexOf(" "+item.trim().toLowerCase()+" ");
            if(y>-1&y>pos)
            {
                pos=y;
            }

        }

        return pos;

    }

    public int countOccurances(List<String> items,String sentence,int max_count)
    {

        int count=0;
        //loop through the list and find the position in the sentence
        for(int i=0;i<items.size();i++)
        {

            String item=items.get(i);
            String s=" "+sentence.trim().toLowerCase()+" ";
            String[] lk=s.split(" "+item.trim().toLowerCase()+" ");
            count+=lk.length-1;
            if(count>=max_count)
            {
                break;
            }

        }

        return count;

    }


}

