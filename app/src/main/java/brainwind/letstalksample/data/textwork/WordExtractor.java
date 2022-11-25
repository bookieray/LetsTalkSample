package brainwind.letstalksample.data.textwork;

import java.util.List;

public class WordExtractor extends Position
{

    public String getWordAtPos(int pos,String sentence)
    {

        String word="";
        if(pos>-1&pos<sentence.length())
        {
            int endindex=sentence.length()-1;
            int y=sentence.indexOf(" ",pos);
            if(y>-1)
            {
                endindex=y;
            }
            word=sentence.substring(pos, endindex);
        }
        return word;

    }

    public String getWordAtPos(List<String> words, String sentence)
    {

        int pos=findPos(words,sentence);
        int matched_num_words=getNumOfWordsOfMatch(words,sentence);
        String word="";
        word=getWordAtPos(pos,matched_num_words-1,sentence);
        return word;

    }

    public String getWordAtPos(int pos,int num_words_in_compound_word,String sentence)
    {

        String word="";

        if(pos>-1&pos<sentence.length())
        {
            int endindex=sentence.length()-1;
            int y=sentence.indexOf(" ",pos);
            if(y>-1)
            {
                endindex=y;
            }
            int hj=endindex;
            int num_found=0;
            while(num_found<num_words_in_compound_word-1&hj>-1)
            {
                int km=sentence.indexOf(" ",hj+1);
                if(km>-1&km>hj&num_found<num_words_in_compound_word-1)
                {
                    hj=km;
                    endindex=km;
                    num_found++;

                }
                else
                {
                    break;
                }

            }
            word=sentence.substring(pos, endindex);
        }
        return word;

    }


    public String getWordBeforeAtPos(int pos,String sentence)
    {

        String word="";

        if(pos>-1&pos<sentence.length())
        {

            String textBeforePos=sentence.substring(0, pos);

            String[] words=textBeforePos.split(" ");

            if(words.length>0)
            {
                word=words[words.length-1];
            }

        }



        return word;

    }

    public String getWordAfterAtPos(int pos,String sentence)
    {

        String word="";

        if(pos>-1&pos<sentence.length())
        {

            String textBeforePos=sentence.substring(pos);

            String[] words=textBeforePos.split(" ");

            if(words.length>1)
            {
                word=words[1];
            }

        }



        return word;

    }

    public String getLastWord(String sentence)
    {

        String word=sentence;
        String[] words=sentence.trim().split(" ");

        if(words.length>0)
        {

            word=words[words.length-1];

        }

        return word;

    }

    public int getNumOfWords(String sentence)
    {

        return sentence.trim().split(" ").length;
    }


}
