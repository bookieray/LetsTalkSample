package brainwind.letstalksample.data.textwork;

import java.util.List;

public class PartExtractor extends WordExtractor
{

    public String ExtractPartBeforePos(int pos,String sentence)
    {

        String part="";

        if(pos>-1&pos<sentence.length())
        {

            part=sentence.substring(0,pos);

        }

        return part;

    }

    public String ExtractPartBeforePos(List<String> list,String sentence)
    {

        String part="";
        int pos=findPos(list,sentence);


        if(pos>-1&pos<sentence.length())
        {

            part=sentence.substring(0,pos);

        }

        return part;

    }

    public String ExtractPartAfterPos(List<String> list, String sentence)
    {

        String part="";
        int pos=findPos(list,sentence);
        int num_words=getNumOfWordsOfMatch(list,sentence);

        if(pos>-1&pos<sentence.length())
        {

            int end=sentence.length()-1;
            end=sentence.indexOf(" ",pos);
            if(end==-1)
            {
                end=sentence.length()-1;
            }

            if(num_words>1)
            {
                int hj=pos;
                int found_white_spaces=0;

                while(found_white_spaces<num_words&hj>-1)
                {

                    hj=sentence.indexOf(" ",hj+1);
                    if(hj>-1)
                    {
                        if(found_white_spaces<num_words)
                        {
                            end=hj;
                        }
                        found_white_spaces++;
                    }


                }

            }
            part=sentence.substring(end);

        }

        return part;

    }

    public String ExtractPartAfterPos(int pos,String sentence)
    {

        String part="";

        if(pos>-1&pos<sentence.length())
        {

            int end=sentence.length()-1;
            end=sentence.indexOf(" ",pos);
            if(end==-1)
            {
                end=sentence.length()-1;
            }

            part=sentence.substring(end);

        }

        return part;

    }

    public String ExtractPartAfterPos(int pos,int num_words,String sentence)
    {

        String part="";

        if(pos>-1&pos<sentence.length())
        {

            int end=sentence.length()-1;
            end=sentence.indexOf(" ",pos);
            if(end==-1)
            {
                end=sentence.length()-1;
            }
            if(num_words>1)
            {
                int hj=pos;
                int found_white_spaces=0;

                while(found_white_spaces<num_words&hj>-1)
                {

                    hj=sentence.indexOf(" ",hj+1);
                    if(hj>-1)
                    {
                        if(found_white_spaces<num_words)
                        {
                            end=hj;
                        }
                        found_white_spaces++;
                    }


                }

            }
            part=sentence.substring(end);

        }

        return part;

    }



}
