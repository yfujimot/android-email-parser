package com.thinkfuji.android_email_parser;

import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.util.Locale;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;

public final class gmailParser {
    public static void main( String[] args ) {

        String host = "imap.gmail.com";
        String username = "wakeupgetrich";
        String password = "yoshio221";

        SimpleDateFormat myFormatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );

        Properties props = System.getProperties();
        props.setProperty( "mail.store.protocol", "imaps" );

        BufferedWriter myWriter = null;
        try {
            myWriter = new BufferedWriter( new FileWriter( "/home/diggler/Desktop/groovy_testing/BigBendHotSprings/BBHSHelpers.csv" ) );
            myWriter.write( "\"Sent Date\",\"Subject\",\"From\",\"E-Mail\",\"Phone Number\",\"Skills & Interests\",\"Comments\"\n" );
        }
        catch ( IOException myIOE ) {
            myIOE.printStackTrace();
        }

        try {

            Session mySession = Session.getDefaultInstance( props, null );
            Store myStore = mySession.getStore("imaps");
            myStore.connect( host, username, password );

            System.out.println( myStore );

            Folder inbox = myStore.getFolder("BBHS Helpers");
            inbox.open(Folder.READ_ONLY);
            Message messages[] = inbox.getMessages();
            System.out.println( messages.length );

            for ( Message message:messages ) {
                Address addys[] = message.getReplyTo();
                for ( Address addy:addys ) {
                    //System.out.println( addy.toString() );
                    if (  addy.toString().trim().equals( "alchemiculture <alchemiculture@culligan.dreamhost.com>" ) ) {
                        if ( message.getSubject().startsWith( "BigBendHotSprings.org Message from" ) ) {
                            String messageSubject =  message.getSubject();
                            String sentDate = myFormatter.format( message.getSentDate() );
                            MimeMessage myMimeMessage = (MimeMessage) message;
                            try {
                                // System.out.println( messageSubject );
                                // System.out.println( sentDate );
                                String messageContent = (String) myMimeMessage.getContent();
                                String messageContentLines[] = messageContent.split("\n");
                                String from = "";
                                String eMail = "";
                                String phoneNumber = "";
                                for ( String line:messageContentLines ) {
                                    if ( line.startsWith( "From:" ) ) {
                                        from = line.split(":")[1].trim();
                                    }
                                    if ( line.startsWith( "Email:" ) ) {
                                        eMail = line.split(":")[1].trim();
                                    }
                                    if ( line.startsWith( "Phone:" ) ) {
                                        phoneNumber = line.split(":")[1].trim();
                                    }
                                }
                                String messageContentSplits[] = messageContent.split("Skills and Interests:");
                                String messageContentSplitsII[] = messageContentSplits[1].split("Comments:");
                                String skillsAndInterests = messageContentSplitsII[0].trim();
                                skillsAndInterests = skillsAndInterests.replaceAll( "\n", "" );
                                skillsAndInterests = skillsAndInterests.replaceAll( "\"", "" );
                                skillsAndInterests = skillsAndInterests.replaceAll( ",", "" );
                                String comments = messageContentSplitsII[1].trim();
                                comments = comments.replaceAll( "\n", "" );
                                comments = comments.replaceAll( "\"", "" );
                                comments = comments.replaceAll( ",", "" );

                                // System.out.println( skillsAndInterests );
                                // System.out.println( comments );

                                myWriter.write( "\"" + sentDate + "\",\"" + messageSubject + "\",\"" + from + "\",\"" + eMail + "\",\"" + phoneNumber + "\",\"" + skillsAndInterests + "\",\"" + comments + "\"\n" );

                            }
                            catch ( IOException myIOE ) {
                                myIOE.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
        catch ( NoSuchProviderException e ) {
            e.printStackTrace();
        }
        catch ( MessagingException e ) {
            e.printStackTrace();
        }

        try {
            myWriter.close();
        }
        catch ( IOException myIOE ) {
            myIOE.printStackTrace();
        }

    }
}