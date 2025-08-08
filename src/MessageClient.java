import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MessageClient {
    private static final int PORT = 3000;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("192.168.0.8", PORT);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream((socket.getOutputStream()));

            Scanner sc = new Scanner(System.in);

            Thread sender = new Thread(new Runnable() {
                private String  msg;

                @Override
                public void run() {
                    try {
                        while (true) {
                            msg = sc.nextLine();
                            out.writeUTF(msg);
                        }

                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
            });


            Thread writer = new Thread(new Runnable() {
                private String msg;
                @Override
                public void run() {
                    try{
                        msg = in.readUTF();

                        while(msg != null){
                            System.out.println(msg);
                            msg = in.readUTF();
                        }

                        System.out.println("Usuario desconectado.");

                    }catch (Exception e){
                        System.out.println(e);
                    }


                }
            });

            sender.start();
            writer.start();


        } catch (IOException e) {
            System.out.println("Ha ocurrido un error" + e);
        }


    }

}
