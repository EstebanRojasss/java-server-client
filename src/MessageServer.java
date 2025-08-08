import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MessageServer {
    private static final int PORT = 3000;


    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);
            Socket socket = server.accept();
            System.out.println("Usuario conectado.");

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            Scanner sc = new Scanner(System.in);

            Thread sender = new Thread(new Runnable() {
                private String msg;

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
                    try {
                        msg = in.readUTF();
                        while (msg != null) {
                            System.out.println(msg);

                            enviarMensajeAPI("user1", "user2", msg);

                            msg = in.readUTF();
                        }

                        System.out.println("Usuario desconectado.");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            });

            sender.start();
            writer.start();


        } catch (IOException e) {
            System.out.println("Error de conexión." + e.getMessage());
        }


    }

    private static void enviarMensajeAPI(String remitente, String destinatario, String contenido) {
        try {
            URL url = new URL("http://localhost:8081/api/mensajes");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"remitente\": \"%s\", \"destinatario\": \"%s\", \"contenido\": \"%s\"}",
                    remitente, destinatario, contenido
            );

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Respuesta del backend: " + code);
        } catch (Exception e) {
            System.out.println("Error al enviar mensaje al backend: " + e.getMessage());
        }
    }


}
