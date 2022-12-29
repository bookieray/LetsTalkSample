package brainwind.letstalksample.features.letstalk.fragments.item;


import android.content.Context;

import com.google.android.exoplayer2.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.Map;

import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.TimeUtilities;

public class Comment
{



    String comment_id="";
    //the comments is based on a single independent conversation
    //or in reference/around another bookie activity
    public boolean is_standalone_convo=true;
    public boolean is_activity_based=false;
    public boolean qoutedFrom=false;
    //Types of comment
    public static final int AGREES=0;
    public static final int DISAGREES=1;
    public static final int QUESTION=2;
    public static final int QOUTE=3;
    public static final int ANSWER=4;
    public static final int NEWS_AD=5;
    //The type of comment
    private int comment_type=AGREES;
    //The comment
    private String comment="";
    //Date and Time aspect for ordering purposes
    private Date createdDate;
    private Date modifiedDate;
    //whether it is a head comment or new topic within the umbrella convo
    private boolean is_new_topic=false;
    //if part of a topic
    private String head_comment_id="";
    //The container, either a conversation or a Bookie Activity
    private String conversation_id="";
    private String activity_id="";
    //Commentator
    private String commentator_name;
    private String commentator_uid;
    private String commentator_profile_path="";
    private int commentator_country_code=27;
    //the number of comments
    private int num_comments=0;
    //news reference
    private boolean hasNewsReference=false;
    private String newsSource="";
    private String newsHeadline="";
    private String sourceLink="";
    //if the comment was shared
    private boolean is_sharing=false;
    private int created_month=new DateTime().getMonthOfYear();
    private String news_source_link="";
    //indicate whether the response comment types
    //values.put(OrgFields.IN_RESPONSE_AGREE,true);
    boolean in_response_agree=false;
    //values.put(OrgFields.IN_RESPONSE_DISAGREE,true);
    boolean in_response_disagree=false;
    //values.put(OrgFields.IN_RESPONSE_QUESTION,true);
    boolean in_response_question=false;
    //values.put(OrgFields.IN_RESPONSE_QUESTION,true);

    //if the comment is expanded or collapsed
    private boolean is_expanded_timestamp=true;
    private boolean is_expanded=true;
    private boolean forced_expand=false;
    private int expanded_lines=0;
    private int leftover_lines=0;


    public Comment(DocumentSnapshot documentSnapshot)
    {

        if(documentSnapshot!=null)
        {

            this.comment_id=documentSnapshot.getId();
            if(documentSnapshot.contains(OrgFields.CONVERSATION_ID))
            {
                this.conversation_id=documentSnapshot.getString(OrgFields.CONVERSATION_ID);
            }
            if(documentSnapshot.contains(OrgFields.IS_STANDALONE_CONVO))
            {
                this.is_standalone_convo=documentSnapshot.getBoolean(OrgFields.IS_STANDALONE_CONVO);
            }
            if(documentSnapshot.contains(OrgFields.IS_ACTIVITY_BASED))
            {
                this.is_activity_based=documentSnapshot.getBoolean(OrgFields.IS_ACTIVITY_BASED);
            }
            if(documentSnapshot.contains(OrgFields.COMMENT_TYPE))
            {
                this.comment_type=documentSnapshot.getLong(OrgFields.COMMENT_TYPE).intValue();
            }
            if(documentSnapshot.contains(OrgFields.COMMENT))
            {
                this.comment=documentSnapshot.getString(OrgFields.COMMENT);
            }
            if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
            {
                this.createdDate=documentSnapshot.getDate(OrgFields.USER_CREATED_DATE);
            }
            if(documentSnapshot.contains(OrgFields.USER_MODIFIED_DATE))
            {
                this.modifiedDate=documentSnapshot.getDate(OrgFields.USER_MODIFIED_DATE);

            }
            if(documentSnapshot.contains(OrgFields.IS_NEW_TOPIC))
            {
                this.is_new_topic=documentSnapshot.getBoolean(OrgFields.IS_NEW_TOPIC);

            }
            if(documentSnapshot.contains(OrgFields.PARENT_COMMENT_ID))
            {
                this.head_comment_id=documentSnapshot.getString(OrgFields.PARENT_COMMENT_ID);
            }
            if(this.is_standalone_convo)
            {
                if(documentSnapshot.contains(OrgFields.CONVERSATION_ID))
                {
                    this.conversation_id=documentSnapshot.getString(OrgFields.CONVERSATION_ID);
                }
            }
            else
            {
                if(documentSnapshot.contains(OrgFields.ACTIVITY_ID))
                {
                    this.activity_id=documentSnapshot.getString(OrgFields.ACTIVITY_ID);
                }
            }

            if(documentSnapshot.contains(OrgFields.COMMENTATER_NAME))
            {
                this.commentator_name=documentSnapshot.getString(OrgFields.COMMENTATER_NAME);
            }
            else
            {

                if(documentSnapshot.contains(OrgFields.USER_FIRSTNAME)
                        &documentSnapshot.contains(OrgFields.USER_LASTNAME))
                {

                    this.commentator_name=documentSnapshot.getString(OrgFields.USER_FIRSTNAME).trim()
                            +" "+documentSnapshot.getString(OrgFields.USER_LASTNAME).trim();

                }
                else
                {
                    this.commentator_name=documentSnapshot.getString(OrgFields.USER_FIRSTNAME).trim();
                }

            }
            if(documentSnapshot.contains(OrgFields.USER_UID))
            {
                this.commentator_uid=documentSnapshot.getString(OrgFields.USER_UID);
            }
            if(documentSnapshot.contains(OrgFields.COMMENTER_PROFILE_IMAGE))
            {
                this.commentator_profile_path=documentSnapshot.getString(OrgFields.COMMENTER_PROFILE_IMAGE);
            }
            else
            {
                if(documentSnapshot.contains(OrgFields.USER_PROFILE_PICTURE))
                {
                    this.commentator_profile_path=documentSnapshot.getString(OrgFields.USER_PROFILE_PICTURE);
                }
            }
            if(documentSnapshot.contains(OrgFields.NUM_COMMENTS))
            {
                this.num_comments=documentSnapshot.getLong(OrgFields.NUM_COMMENTS).intValue();
            }

            //News Source
            if(documentSnapshot.contains(OrgFields.HAS_NEWS_REFERENCE))
            {
                this.hasNewsReference=documentSnapshot.getBoolean(OrgFields.HAS_NEWS_REFERENCE);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_SOURCE))
            {
                this.newsSource=documentSnapshot.getString(OrgFields.NEWS_SOURCE);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_HEADLINE))
            {
                this.newsHeadline=documentSnapshot.getString(OrgFields.NEWS_HEADLINE);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_SOURCELINK))
            {
                this.news_source_link=documentSnapshot.getString(OrgFields.NEWS_SOURCELINK);
            }

            if(documentSnapshot.contains(OrgFields.IN_RESPONSE_AGREE))
            {
                this.in_response_agree=documentSnapshot.getBoolean(OrgFields.IN_RESPONSE_AGREE);
            }
            if(documentSnapshot.contains(OrgFields.IN_RESPONSE_DISAGREE))
            {
                this.in_response_disagree=documentSnapshot.getBoolean(OrgFields.IN_RESPONSE_DISAGREE);
            }
            if(documentSnapshot.contains(OrgFields.IN_RESPONSE_QUESTION))
            {
                this.in_response_question=documentSnapshot.getBoolean(OrgFields.IN_RESPONSE_QUESTION);
            }

            if(documentSnapshot.contains(OrgFields.MONTH))
            {
                this.month=documentSnapshot.getLong(OrgFields.MONTH).intValue();
                Log.i("pushjdla","month="+month);
            }
            else
            {
                if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
                {
                    this.createdDate=documentSnapshot.getDate(OrgFields.USER_CREATED_DATE);
                    LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
                    this.month=localDateTime.getMonthOfYear();

                }
            }
            if(documentSnapshot.contains(OrgFields.DAY))
            {
                this.day=documentSnapshot.getLong(OrgFields.DAY).intValue();
            }
            else
            {

                if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
                {
                    this.createdDate=documentSnapshot.getDate(OrgFields.USER_CREATED_DATE);
                    LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
                    this.day=localDateTime.getDayOfMonth();

                }

            }
            if(documentSnapshot.contains(OrgFields.YEAR))
            {
                this.year=documentSnapshot.getLong(OrgFields.YEAR).intValue();
            }
            else
            {
                if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
                {
                    this.createdDate=documentSnapshot.getDate(OrgFields.USER_CREATED_DATE);
                    LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
                    this.year=localDateTime.getYear();

                }
            }

            if(this.createdDate!=null)
            {

                LocalDateTime localDateTime=new LocalDateTime(this.createdDate);
                if(localDateTime.getMonthOfYear()
                        !=this.getMonth())
                {
                    this.setMonth(localDateTime.getMonthOfYear());
                }
                if(localDateTime.getYear()
                        !=this.getYear())
                {
                    this.setMonth(localDateTime.getYear());
                }
                if(localDateTime.getDayOfMonth()
                        !=this.getDay())
                {
                    this.setMonth(localDateTime.getDayOfMonth());
                }

            }

            if(documentSnapshot.contains(OrgFields.REPLIED_COMMENTATOR_NAME))
            {
                String reply_commentator_name=documentSnapshot.getString(OrgFields.REPLIED_COMMENTATOR_NAME);
                this.setReply_comment_name(reply_commentator_name);
            }
            if(documentSnapshot.contains(OrgFields.REPLIED_COMMENT))
            {
                String reply_comment=documentSnapshot.getString(OrgFields.REPLIED_COMMENT);
                this.setReply_comment(reply_comment);
            }
            if(documentSnapshot.contains(OrgFields.REPLIED_COMMENT_ID))
            {
                String reply_comment_id=documentSnapshot.getString(OrgFields.REPLIED_COMMENT_ID);
                this.setReply_comment_id(reply_comment_id);
            }
            if(documentSnapshot.contains(OrgFields.REPLIED_COMMENT_TYPE))
            {
                this.reply_comment_type=documentSnapshot.getLong(OrgFields.REPLIED_COMMENT_TYPE).intValue();
            }

        }

    }
    public Comment(Map<String,Object> values)
    {

        if(values!=null)
        {

            //the person commentating
            if(values.containsKey(OrgFields.USER_UID))
            {
                this.commentator_uid=values.get(OrgFields.USER_UID).toString();
            }
            if(values.containsKey(OrgFields.COMMENTATER_NAME))
            {
                this.commentator_name=values.get(OrgFields.COMMENTATER_NAME).toString();
            }
            if(values.containsKey(OrgFields.USER_COUNTRY_CODE))
            {
                this.commentator_country_code=(int)values.get(OrgFields.USER_COUNTRY_CODE);
            }
            if(values.containsKey(OrgFields.COMMENTER_PROFILE_IMAGE))
            {
                this.commentator_profile_path=values.get(OrgFields.COMMENTER_PROFILE_IMAGE).toString();
            }
            //whether it is under a head comment or topic
            if(values.containsKey(OrgFields.IS_NEW_TOPIC))
            {
                this.is_new_topic=Boolean.parseBoolean(values.get(OrgFields.IS_NEW_TOPIC).toString());
                if(this.is_new_topic==false)
                {
                    if(values.containsKey(OrgFields.PARENT_COMMENT_ID))
                    {
                        this.head_comment_id=values.get(OrgFields.PARENT_COMMENT_ID).toString();
                    }
                }
            }
            //comment type
            if(values.containsKey(OrgFields.COMMENT_TYPE))
            {
                this.comment_type=(int)values.get(OrgFields.COMMENT_TYPE);
            }
            if(values.containsKey(OrgFields.COMMENT))
            {
                this.comment=values.get(OrgFields.COMMENT).toString();
            }
            //check if it was shared
            if(values.containsKey(OrgFields.IS_NEW_TOPIC))
            {
                this.is_sharing=Boolean.parseBoolean(values.get(OrgFields.IS_NEW_TOPIC).toString());
            }
            //the timestamp for the comment
            if(values.containsKey(OrgFields.USER_CREATED_DATE))
            {
                this.createdDate= Timestamp.now().toDate();
            }
            if(values.containsKey(OrgFields.USER_MODIFIED_DATE))
            {
                this.modifiedDate=Timestamp.now().toDate();
            }
            if(values.containsKey(OrgFields.CREATED_MONTH))
            {
                this.created_month=(int)values.get(OrgFields.CREATED_MONTH);
            }

            //comment belong to which conversation
            if(values.containsKey(OrgFields.CONVERSATION_ID))
            {
                this.conversation_id=values.get(OrgFields.CONVERSATION_ID).toString();
            }
            if(values.containsKey(OrgFields.IS_STANDALONE_CONVO))
            {
                this.is_standalone_convo=Boolean.parseBoolean(values.get(OrgFields.IS_STANDALONE_CONVO).toString());
            }
            if(values.containsKey(OrgFields.IS_ACTIVITY_BASED))
            {
                this.is_activity_based=Boolean.parseBoolean(values.get(OrgFields.IS_ACTIVITY_BASED).toString());
            }
            if(values.containsKey(OrgFields.ACTIVITY_ID))
            {
                this.activity_id=values.get(OrgFields.ACTIVITY_ID).toString();
            }

            if(values.containsKey(OrgFields.NUM_COMMENTS))
            {
                this.num_comments=Integer.parseInt(values.get(OrgFields.NUM_COMMENTS).toString());
            }

            //News Source
            if(values.containsKey(OrgFields.HAS_NEWS_REFERENCE))
            {
                this.hasNewsReference=Boolean.parseBoolean(values.get(OrgFields.HAS_NEWS_REFERENCE).toString());
            }
            if(values.containsKey(OrgFields.NEWS_SOURCE))
            {
                this.newsSource=values.get(OrgFields.NEWS_SOURCE).toString();
            }
            if(values.containsKey(OrgFields.NEWS_HEADLINE))
            {
                this.newsHeadline=values.get(OrgFields.NEWS_HEADLINE).toString();
            }
            if(values.containsKey(OrgFields.NEWS_SOURCELINK))
            {
                this.news_source_link=values.get(OrgFields.NEWS_SOURCELINK).toString();
            }



        }

    }

    public Comment(Comment comment)
    {

        if(comment!=null)
        {

            this.comment_id=comment.getComment_id();
            this.is_standalone_convo=comment.is_standalone_convo;
            this.is_activity_based=comment.is_activity_based;
            this.comment_type=comment.getComment_type();
            this.comment=comment.getComment();
            this.createdDate=comment.createdDate;
            this.modifiedDate=comment.modifiedDate;
            this.is_new_topic=comment.is_new_topic;
            this.setHead_comment_id(comment.getHead_comment_id());
            if(this.is_standalone_convo)
            {
                this.conversation_id=comment.conversation_id;
            }
            else
            {
                this.activity_id=comment.activity_id;
            }

            this.commentator_name=comment.commentator_name;
            this.commentator_uid=comment.commentator_uid;
            this.commentator_profile_path=comment.commentator_profile_path;
            this.num_comments=comment.num_comments;

            //News Source
            this.hasNewsReference=comment.hasNewsReference;
            this.newsSource=comment.newsSource;
            this.newsHeadline=comment.newsHeadline;
            this.news_source_link=comment.news_source_link;

        }

    }

    public Comment() {

    }

    public boolean isIs_standalone_convo() {
        return is_standalone_convo;
    }

    public boolean isIs_activity_based() {
        return is_activity_based;
    }

    //The Comment Type
    public void setComment_type(int comment_type) {
        this.comment_type = comment_type;
    }
    public int getComment_type() {
        return comment_type;
    }
    //Comment
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    //Date and Time aspect for ordering purposes
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    //topic
    public boolean isIs_new_topic() {
        return is_new_topic;
    }

    public String getHead_comment_id() {
        return head_comment_id;
    }

    public void setHead_comment_id(String head_comment_id) {
        this.head_comment_id = head_comment_id;
    }
    //The container, either a conversation or a Bookie Activity
    public String getConversation_id() {
        return conversation_id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public String getCommentator_name() {
        return commentator_name;
    }

    public String getCommentator_uid() {
        return commentator_uid;
    }

    public String getCommentator_profile_path() {
        return commentator_profile_path;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public boolean isHasNewsReference() {
        return hasNewsReference;
    }

    public void setHasNewsReference(boolean hasNewsReference) {
        this.hasNewsReference = hasNewsReference;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getNewsHeadline() {
        return newsHeadline;
    }

    public void setNewsHeadline(String newsHeadline) {
        this.newsHeadline = newsHeadline;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setIs_standalone_convo(boolean is_standalone_convo) {
        this.is_standalone_convo = is_standalone_convo;
    }

    public void setIs_activity_based(boolean is_activity_based) {
        this.is_activity_based = is_activity_based;
    }

    public void setIs_new_topic(boolean is_new_topic) {
        this.is_new_topic = is_new_topic;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public void setCommentator_name(String commentator_name) {
        this.commentator_name = commentator_name;
    }

    public void setCommentator_uid(String commentator_uid) {
        this.commentator_uid = commentator_uid;
    }

    public void setCommentator_profile_path(String commentator_profile_path) {
        this.commentator_profile_path = commentator_profile_path;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public int getCommentator_country_code() {
        return commentator_country_code;
    }

    public void setCommentator_country_code(int commentator_country_code) {
        this.commentator_country_code = commentator_country_code;
    }

    public boolean isIs_sharing() {
        return is_sharing;
    }

    public void setIs_sharing(boolean is_sharing) {
        this.is_sharing = is_sharing;
    }

    public int getCreated_month() {
        return created_month;
    }

    public void setCreated_month(int created_month) {
        this.created_month = created_month;
    }

    public String getNews_source_link() {
        return news_source_link;
    }

    public void setNews_source_link(String news_source_link) {
        this.news_source_link = news_source_link;
    }

    public boolean isQoutedFrom() {
        return qoutedFrom;
    }

    public void setQoutedFrom(boolean qoutedFrom) {
        this.qoutedFrom = qoutedFrom;
    }

    private int reply_comment_type=0;
    public boolean isIn_response_agree() {

        if(reply_comment_type==Comment.AGREES)
        {
            this.in_response_agree=true;
        }
        else
        {
            this.in_response_agree=false;
        }
        return in_response_agree;

    }

    public void setIn_response_agree(boolean in_response_agree) {
        this.in_response_agree = in_response_agree;
    }

    public boolean isIn_response_disagree() {
        if(reply_comment_type==Comment.DISAGREES)
        {
            this.in_response_disagree=true;
        }
        else
        {
            this.in_response_disagree=false;
        }
        return in_response_disagree;
    }

    public void setIn_response_disagree(boolean in_response_disagree) {
        this.in_response_disagree = in_response_disagree;
    }

    public boolean isIn_response_question() {

        if(reply_comment_type==Comment.QUESTION)
        {
            this.in_response_question=true;
        }
        else
        {
            this.in_response_question=false;
        }
        return in_response_question;
    }

    public int getReply_comment_type() {
        return reply_comment_type;
    }

    public void setReply_comment_type(int reply_comment_type) {
        this.reply_comment_type = reply_comment_type;
    }

    public void setIn_response_question(boolean in_response_question) {
        this.in_response_question = in_response_question;
    }

    public boolean isIs_expanded_timestamp() {
        return is_expanded_timestamp;
    }

    public void setIs_expanded_timestamp(boolean is_expanded_timestamp) {
        this.is_expanded_timestamp = is_expanded_timestamp;
    }

    public int getExpanded_lines() {
        return expanded_lines;
    }

    public void setExpanded_lines(int expanded_lines) {
        this.expanded_lines = expanded_lines;
    }

    public boolean isIs_expanded() {
        return is_expanded;
    }

    public void setIs_expanded(boolean is_expanded) {
        this.is_expanded = is_expanded;
    }

    public int getLeftover_lines() {
        return leftover_lines;
    }

    public void setLeftover_lines(int leftover_lines) {
        this.leftover_lines = leftover_lines;
    }

    public boolean isForced_expand() {
        return forced_expand;
    }

    public void setForced_expand(boolean forced_expand) {
        this.forced_expand = forced_expand;
    }

    private boolean is_timestamp=false;

    public boolean isIs_timestamp() {
        return is_timestamp;
    }

    public void setIs_timestamp(boolean is_timestamp) {
        this.is_timestamp = is_timestamp;
    }

    private int day=new LocalDateTime().getDayOfMonth();
    private int month=new LocalDateTime().getMonthOfYear();
    private int year=new LocalDateTime().getYear();

    //no need for
    public boolean isOnTheSameDay(Comment prev_comment)
    {

        //perform a check to correct the comment
        correctAnotherComment(prev_comment);
        correct();
        return this.day==prev_comment.day
                &this.month==prev_comment.month
                &this.year==prev_comment.year;
    }

    public void correctAnotherComment(Comment comment)
    {


        if(new LocalDateTime(comment.getCreatedDate()).getMonthOfYear()
                !=comment.getMonth())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getMonthOfYear());
        }
        if(new LocalDateTime(comment.getCreatedDate()).getYear()
                !=comment.getYear())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getYear());
        }
        if(new LocalDateTime(comment.getCreatedDate()).getDayOfMonth()
                !=comment.getDay())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getDayOfMonth());
        }



    }
    public void correct()
    {

        if(new LocalDateTime(this.getCreatedDate()).getMonthOfYear()
                !=this.getMonth())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(this.getCreatedDate());
            this.setMonth(localDateTime.getMonthOfYear());
        }
        if(new LocalDateTime(this.getCreatedDate()).getYear()
                !=this.getYear())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(this.getCreatedDate());
            this.setMonth(localDateTime.getYear());
        }
        if(new LocalDateTime(this.getCreatedDate()).getDayOfMonth()
                !=this.getDay())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(this.getCreatedDate());
            this.setMonth(localDateTime.getDayOfMonth());
        }

    }

    public int getDay() {
        if(this.getCreatedDate()!=null)
        {
            LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
            if(localDateTime.getDayOfMonth()!=this.day)
            {
                this.day=localDateTime.getDayOfMonth();
            }
        }
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {

        if(this.getCreatedDate()!=null)
        {
            LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
            if(localDateTime.getMonthOfYear()!=this.month)
            {
                this.month=localDateTime.getMonthOfYear();
            }
        }

        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        if(this.getCreatedDate()!=null)
        {
            LocalDateTime localDateTime=new LocalDateTime(this.getCreatedDate());
            if(localDateTime.getYear()!=this.year)
            {
                this.year=localDateTime.getYear();
            }
        }
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private int num_comments_agree_to_timestamp=0;
    private int num_comments_disagree_to_timestamp=0;
    private int num_comments_question_to_timestamp=0;

    public int IncrementAgreeOnTimeStamp()
    {
        return this.num_comments_agree_to_timestamp++;
    }
    public int IncrementDisAgreeOnTimeStamp()
    {
        return this.num_comments_disagree_to_timestamp++;
    }
    public int IncrementQuestionOnTimeStamp()
    {
        return this.num_comments_question_to_timestamp++;
    }

    public static final int MOSTLY_DISAGREE=0;
    public static final int MOSTLY_AGREE=1;
    public static final int MOSTLY_ASK=2;
    public static final int HALF_AGREE=3;

    //this is
    private int status=MOSTLY_AGREE;
    public int getStatus()
    {

        Log.i("getStatus",""+num_comments_agree_to_timestamp+" ");
        if(num_comments_agree_to_timestamp>num_comments_disagree_to_timestamp
                &num_comments_agree_to_timestamp>num_comments_question_to_timestamp)
        {
            this.status=MOSTLY_AGREE;
            return this.status;
        }
        if(num_comments_disagree_to_timestamp>num_comments_agree_to_timestamp
                &num_comments_disagree_to_timestamp>num_comments_question_to_timestamp)
        {
            this.status=MOSTLY_DISAGREE;
            return this.status;
        }
        if(num_comments_question_to_timestamp>num_comments_agree_to_timestamp
                &num_comments_question_to_timestamp>num_comments_disagree_to_timestamp)
        {
            this.status=MOSTLY_ASK;
            return this.status;
        }
        else if(num_comments_agree_to_timestamp==num_comments_disagree_to_timestamp
                &num_comments_agree_to_timestamp>num_comments_question_to_timestamp)
        {
            this.status=HALF_AGREE;
            return this.status;
        }
        else
        {
            this.status=MOSTLY_AGREE;
            return this.status;
        }


    }

    //tracks the comment this comment will group under as the timestamp for this comment
    String timestamp_comment_id="";

    public boolean isToday(long offset)
    {


        correct();
        LocalDateTime localDateTime=new LocalDateTime(offset);
        boolean isToday=false;

        isToday=localDateTime.getYear()==this.year
                &localDateTime.getMonthOfYear()==this.month
                &localDateTime.getDayOfMonth()==this.day;

        return isToday;
    }

    public void setTimestamp(int day,int month,int year)
    {

        this.setDay(day);
        this.setMonth(month);
        this.setYear(year);
        LocalDateTime localDateTime=new LocalDateTime();
        this.createdDate=localDateTime.withDayOfMonth(day)
                .withMonthOfYear(month)
                .withYear(year)
                .toDate();

    }

    public void setTimestamp(TMDay tmDay1)
    {
        int day=Integer.parseInt(tmDay1.getDay());
        int month=Integer.parseInt(tmDay1.getMonth());
        int year=tmDay1.getYear();
        this.setDay(day);
        this.setMonth(month);
        this.setYear(year);
        LocalDateTime localDateTime=new LocalDateTime();
        this.createdDate=localDateTime.withDayOfMonth(day)
                .withMonthOfYear(month)
                .withYear(year)
                .toDate();

    }

    private String reply_comment_name="";
    private String reply_comment="";
    private String reply_commentator_id="";
    private String reply_comment_id="";

    public String getReply_comment_name() {
        return reply_comment_name;
    }

    public void setReply_comment_name(String reply_comment_name) {
        this.reply_comment_name = reply_comment_name;
    }

    public String getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(String reply_comment) {
        this.reply_comment = reply_comment;
    }

    public String getReply_commentator_id() {
        return reply_commentator_id;
    }

    public void setReply_commentator_id(String reply_commentator_id) {
        this.reply_commentator_id = reply_commentator_id;
    }

    public String getReply_comment_id() {
        return reply_comment_id;
    }

    public void setReply_comment_id(String reply_comment_id) {
        this.reply_comment_id = reply_comment_id;
    }

    boolean isFromStartingSign=false;
    public void setTimestamp(String starting_sign)
    {

        starting_sign=starting_sign.replace(".","/");
        String[] parts=starting_sign.split("/");
        String year_txt="";
        String month_txt="";
        String day_txt="";
        String hour_txt="";
        String min_txt="";
        String sec_txt="";

        Log.i("setTimestamp",starting_sign+" "+parts.length);
        if(parts.length>=1)
        {
            year_txt=parts[0];
        }
        if(parts.length>=2)
        {
            month_txt=parts[1];
        }
        if(parts.length>=3)
        {
            day_txt=parts[2];
        }
        if(parts.length>=4)
        {
            hour_txt=parts[3];
        }
        if(parts.length>=5)
        {
            min_txt=parts[4];
        }
        if(parts.length>=6)
        {
            sec_txt=parts[5];
        }

        int year=0;
        int month=0;
        int day=0;
        int hour=0;
        int min=0;
        int sec=0;

        if(year_txt.isEmpty()==false)
        {
            try {
                year=Integer.parseInt(year_txt);
            }
            catch(Exception exception)
            {

            }
        }
        if(month_txt.isEmpty()==false)
        {
            try {
                month=Integer.parseInt(month_txt);
            }
            catch(Exception exception)
            {

            }
        }
        if(day_txt.isEmpty()==false)
        {
            try {
                day=Integer.parseInt(day_txt);
            }
            catch(Exception exception)
            {

            }
        }
        if(hour_txt.isEmpty()==false)
        {
            try {
                hour=Integer.parseInt(hour_txt);
            }
            catch(Exception exception)
            {

            }
        }
        if(min_txt.isEmpty()==false)
        {
            try {
                min=Integer.parseInt(min_txt);
            }
            catch(Exception exception)
            {

            }
        }
        if(sec_txt.isEmpty()==false)
        {
            try {
                sec=Integer.parseInt(sec_txt);
            }
            catch(Exception exception)
            {

            }
        }

        setYear(year);
        setMonth(month);
        setDay(day);
        setHour(hour);
        setMin(min);
        setSec(sec);
        setFromStartingSign(true);

        Log.i("setTimestamp","year="+year+" month="+month+" day="+day);


    }

    int hour=0;
    int min=0;
    int sec=0;
    public boolean isFromStartingSign() {
        return isFromStartingSign;
    }

    public void setFromStartingSign(boolean fromStartingSign) {
        isFromStartingSign = fromStartingSign;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public String getTimestamp()
    {
        return getYear()+"-"+getMonth()+"-"+getDay();

    }

    boolean isSent=true;

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    int numOFCommentsRead=0;

    public int getNumOFCommentsRead() {
        return numOFCommentsRead;
    }

    public void setNumOFCommentsRead(int numOFCommentsRead) {
        this.numOFCommentsRead = numOFCommentsRead;
    }

    public String getDateTimeStr()
    {

        android.text.format.DateFormat df =
                new android.text.format.DateFormat();
        String outputx1=df.format("LLL dd,E yyyy hh:mm:ss a",
                        this.getCreatedDate())
                .toString();
        return outputx1;
    }
    public String getTimeStr()
    {

        if(this.getCreatedDate()!=null)
        {
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String outputx1=df.format("hh:mm:ss a",
                            this.getCreatedDate())
                    .toString();
            return outputx1;
        }
        else
        {
            return "";
        }
    }
    public String getDateStr()
    {

        if(this.getCreatedDate()!=null)
        {
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String outputx1=df.format("LLL dd,E yyyy",
                            this.getCreatedDate())
                    .toString();
            return outputx1;
        }
        else
        {
            return "";
        }
    }

    private boolean isItAd=false;

    public boolean isItAd() {
        return isItAd;
    }

    public void setItAd(boolean itAd) {
        isItAd = itAd;
    }

    private int showAdIndex=0;

    public int getShowAdIndex() {
        return showAdIndex;
    }

    public void setShowAdIndex(int showAdIndex) {
        this.showAdIndex = showAdIndex;
    }

    private int adapter_position=0;

    public int getAdapter_position() {
        return adapter_position;
    }

    public void setAdapter_position(int adapter_position) {
        this.adapter_position = adapter_position;
    }

    public String getTimeLabel(Context context)
    {
        Memory memory=new Memory(context);
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

        if(jnm.isEmpty()==false)
        {

            long localtimeoffset=Long.parseLong(jnm);
            long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;

            org.joda.time.LocalDateTime localDateTime
                    =new org.joda.time.LocalDateTime(estimatedServerTimeMs);
            android.util.Log.w("jskasd", "offset="+localtimeoffset
                    +" estimatedServerTimeMs="+estimatedServerTimeMs
                    +" "+localDateTime.getDayOfMonth()+" "+localDateTime.getMonthOfYear()
                    +" "+localDateTime.getYear()
                    +" "+String.valueOf(localtimeoffset));

            String timelabel= TimeUtilities.getTimeLabel(localDateTime,this);
            Log.i("tinsja","timelabel="+timelabel+" "+localDateTime.getDayOfMonth()
                    +" "+localDateTime.getMonthOfYear()+" "+localDateTime.getYear());
            return timelabel;
        }
        else
        {
            return "";
        }

    }


}



