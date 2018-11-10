
package smtpskeleton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String mailServer = "webmail.buet.ac.bd";
        InetAddress mailHost = InetAddress.getByName(mailServer);
        InetAddress localHost = InetAddress.getLocalHost();
        BufferedReader in = null;
        PrintWriter pr = null;
        //Closed State
        while(true) {
            Socket smtpSocket = new Socket(mailHost, 25);
            in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            pr = new PrintWriter(smtpSocket.getOutputStream(), true);

            int time = (int) System.currentTimeMillis();
            String initialID = in.readLine();
            System.out.println(initialID);
            int time2 = (int) System.currentTimeMillis();
            int duration = (time2 - time)/1000;
            if(duration >=20) System.exit(0);

            //System.out.println(duration);
            if (initialID.startsWith("2")) {
                System.out.println("Connection Established.");
                break;
            } else {
                System.out.println("Connection failed!!");
            }
        }

        //BEGIN State
        while(true) {
            pr.println("HELO " + localHost.getHostName());
            //pr.flush();
            int time = (int) System.currentTimeMillis();
            String welcome = in.readLine();
            System.out.println(welcome);
            int time2 = (int) System.currentTimeMillis();
            int duration = (time2 - time)/1000;
            if(duration >=20) System.exit(0);

            if(!welcome.startsWith("2")){
                System.out.println("Error sending HELO message!");
            }
            else{
                break;
            }
        }
        // TODO code application logic here

        System.out.println("\nEnter RSET to reset from anywhere");

        while(true) {
            Scanner sc = new Scanner(System.in);

            // WAIT STATE

            String from = "";
            while (true) {
                System.out.println("\nEnter your mail address: ");
                from = "";
                from = sc.nextLine();
                //System.out.println("\n"+from+ "\n");
                if (from.equals("RSET")) {
                    pr.println("RSET");
                    pr.flush();

                    int time = (int) System.currentTimeMillis();
                    String reset = in.readLine();
                    System.out.println(reset);
                    int time2 = (int) System.currentTimeMillis();
                    int duration = (time2 - time)/1000;
                    if(duration >=20) System.exit(0);

                } else {
                    pr.println("MAIL FROM:<" + from + ">");
                    pr.flush();

                    int time = (int) System.currentTimeMillis();
                    String ok = in.readLine();
                    System.out.println(ok);
                    int time2 = (int) System.currentTimeMillis();
                    int duration = (time2 - time)/1000;
                    if(duration >=20) System.exit(0);

                    if (!ok.startsWith("2")) {
                        System.out.println("Error recognizing sender");
                    } else {
                        break;
                    }
                }
            }

            // WAIT ENDS

            //Recipients Set

            String to = "";
            String rec = "";
            ArrayList<String> multiRec = new ArrayList<>();
            int resFlag = 0;
            int errFlag = 0;
            while (true) {
                System.out.println("How many receivers?: ");
                rec = sc.nextLine();
                if (rec.equals("RSET")) {
                    pr.println("RSET");
                    pr.flush();

                    int time = (int) System.currentTimeMillis();
                    String reset = in.readLine();
                    System.out.println(reset);
                    int time2 = (int) System.currentTimeMillis();
                    int duration = (time2 - time)/1000;
                    if(duration >=20) System.exit(0);

                    resFlag = 1;
                    break;
                } else {
                    System.out.println("Enter receivers mail addresses: ");
                    int receive = Integer.parseInt(rec);
                    for (int i = 0; i < receive; i++) {
                        System.out.println("Receiver" + (i + 1) + ": ");

                        to = sc.next();
                        if (to.equals("RSET")) {
                            pr.println("RSET");
                            pr.flush();

                            int time = (int) System.currentTimeMillis();
                            String reset = in.readLine();
                            System.out.println(reset);
                            int time2 = (int) System.currentTimeMillis();
                            int duration = (time2 - time)/1000;
                            if(duration >=20) System.exit(0);

                            resFlag = 1;         //  for acknowledging reset
                            break;              //     breaks the for loop
                        }else{
                            multiRec.add(to);
                            pr.println("RCPT TO:<" + to + ">");
                            pr.flush();

                            int time = (int) System.currentTimeMillis();
                            String accept = in.readLine();
                            System.out.println(accept);
                            int time2 = (int) System.currentTimeMillis();
                            int duration = (time2 - time)/1000;
                            if(duration >=20) System.exit(0);

                            if (!accept.startsWith("2")) {
                                System.out.println("Error recognizing the receiver!!");
                                break;
                                ///breaks the for loop and the small while loop is continued as there is a error in this state
                            } else {
                                errFlag = 1;
                                ///Indicates that there are no errors..
                            }
                        }
                    }
                    if(resFlag == 1 || errFlag == 1) break;//break the small while loop
                }
            }
            if (resFlag == 1) continue;     //RESET has been committed and starting from begin of big while loop

            // Recipient Set closed

            pr.println("DATA");
            int time = (int) System.currentTimeMillis();
            String enter = in.readLine();
            System.out.println(enter);
            int time2 = (int) System.currentTimeMillis();
            int duration = (time2 - time)/1000;
            if(duration >=20) System.exit(0);

            //Writing mail

            Scanner subsc = new Scanner(System.in);
            while (true) {
                System.out.println("Subject: ");
                String sub = subsc.nextLine();

                if (sub.equals("RSET")) {
                    pr.println("RSET");
                    pr.flush();

                    int rs = (int) System.currentTimeMillis();
                    String reset = in.readLine();
                    System.out.println(reset);
                    int rs2 = (int) System.currentTimeMillis();
                    int dr = (rs2 - rs)/1000;
                    if(dr >=20) System.exit(0);

                    //resFlag = 1;
                    break;
                } else {
                    System.out.println("Enter your message: ");
                    String mail = "";
                    String line = "";

                    while (sc.hasNextLine()) {
                        line = sc.nextLine();
                        if (line.equals(".")) {
                            break;
                        }
                        mail += line + "\n";
                    }
                    if (rec.equals("1")) {
                        //System.out.println("From: " + from + "\nTo: " + to + "\nSubject: " + sub + "\n" + mail + ".\n");
                        pr.println("Subject: " + sub);
                        pr.println("From: " + from);
                        pr.println("To: "+ to);
                        pr.println("\n" + mail);
                        pr.println(".");
                        pr.flush();

                        int tim = (int) System.currentTimeMillis();
                        String ser = in.readLine();
                        System.out.println(ser);
                        int tim2 = (int) System.currentTimeMillis();
                        int dura = (tim2 - tim)/1000;
                        if(dura >=20) System.exit(0);

                        pr.println("RSET");
                        //pr.flush();

                        int rs = (int) System.currentTimeMillis();
                        String reset = in.readLine();
                        System.out.println(reset);
                        int rs2 = (int) System.currentTimeMillis();
                        int dr = (rs2 - rs)/1000;
                        if(dr >=20) System.exit(0);

                        if(!ser.startsWith("2")){
                            System.out.println("Error writing the message!!");
                        }
                        else {
                            break;
                        }
                    } else {
                        String cc = "";
                        for (int i = 1; i < multiRec.size(); i++) {
                            cc += multiRec.get(i) + ",";
                        }
                        pr.println("Subject: " + sub);
                        pr.println("From: " + from);
                        pr.println("To: " + multiRec.get(0));
                        pr.println("Cc: " + cc );
                        pr.println("\n" + mail);
                        pr.println(".");
                        pr.flush();

                        int tim = (int) System.currentTimeMillis();
                        String ser = in.readLine();
                        System.out.println(ser);
                        int tim2 = (int) System.currentTimeMillis();
                        int dura = (tim2 - tim)/1000;
                        if(dura >=20) System.exit(0);

                        pr.println("RSET");
                        //pr.flush();
                        int rs = (int) System.currentTimeMillis();
                        String reset = in.readLine();
                        System.out.println(reset);
                        int rs2 = (int) System.currentTimeMillis();
                        int dr = (rs2 - rs)/1000;
                        if(dr >=20) System.exit(0);

                        if(!ser.startsWith("2")){
                            System.out.println("Error writing the message!!");
                        }
                        else {
                            break;
                        }
                    }
                }
            }

            //Writing mail closed

            System.out.println("Do You Want to Quit?[Y/N]: ");
            String quit = sc.nextLine();
            if(quit.equals("Y")){
                pr.println("QUIT");
                //pr.flush();
                int tim = (int) System.currentTimeMillis();
                String bye = in.readLine();
                System.out.println(bye);
                int tim2 = (int) System.currentTimeMillis();
                int dura = (tim2 - tim)/1000;
                if(dura >=20) System.exit(0);
                System.out.println("Closing Connection with "+ localHost.getHostName());
                break;
            }
            else{
                continue;
            }

        }

    }
}
