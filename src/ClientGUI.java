import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;

    private ServerWindow server; // это связь с объектом сервера
    private boolean connected; // Хьюстон! мы вас слышим!
    private String name; // ну это то, во что превращается написанный нами tfLogin и отправляется серверу вместе с сообщением

    JTextArea log; // область текста в окошке
    JTextField tfIPAddress, tfPort, tfLogin, tfMessage; // ip-адрес и порт тут явно избыточны
    JPasswordField password; // если упрощать, то можно бы и без пароля обойтись
    JButton btnLogin, btnSend;
    JPanel headerPanel; // ???

    public ClientGUI(ServerWindow server){
        this.server = server;

        setSize(WIDTH, HEIGHT);
        // setResizable(false); // а вот не надо!
        setTitle("Chat client");
        setLocation(server.getX() - 500, server.getY());

        createPanel();

        setVisible(true);
    }

    // обертка над методом appendLog() а зачем она нужна?
    public void answer(String text){
        appendLog(text);
    }

    // ну тут мудреная комбинация подключения клиента к серверу и сервера к клиенту. Просто притча о курице и яйце
    private void connectToServer() {
        if (server.connectUser(this)){
            appendLog("Вы успешно подключились!\n");
            // headerPanel.setVisible(false); // не понял, зачем это нужно, поэтому в бан его!
            connected = true;
            name = tfLogin.getText();
            String log = server.getLog();
            if (log != null){
                appendLog(log);
            }
        } else {
            appendLog("Подключение не удалось");
        }
    }

    // облобызавшись и бросив прощальные взгляды, клиент и сервер расстаются!
    public void disconnectFromServer() {
        if (connected) {
            // headerPanel.setVisible(true); // не поняв, что такое headerPanel - я не постиг Дзен!
            connected = false;
            server.disconnectUser(this);
            appendLog("Вы были отключены от сервера!");
        }
    }

    // текст в окошке клиента отправляется серверу
    public void message(){
        if (connected){
            String text = tfMessage.getText();
            if (!text.equals("")){
                server.message(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("Нет подключения к серверу");
        }

    }

    private void appendLog(String text){
        log.append(text + "\n");
    }


    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH); // вот тут понятно, для чего headerPanel, но и только...
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    private Component createHeaderPanel(){
        headerPanel = new JPanel(new FlowLayout());
        //tfIPAddress = new JTextField("127.0.0.1");
        //tfPort = new JTextField("8189");
        tfLogin = new JTextField("Ivan Ivanovich");
        //password = new JPasswordField("123456");
        btnLogin = new JButton("login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        //headerPanel.add(tfIPAddress);
        //headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        //headerPanel.add(password);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    // тут специфично и хитро
    private Component createLog(){
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    // нижняя панель для сообщений и кнопки их отправки
    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n'){
                    message();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    // какой-то хитрый метод для закрытия окна, а я его закоментил - и ничего вроде не изменилось
//    @Override
//    protected void processWindowEvent(WindowEvent e) {
//        if (e.getID() == WindowEvent.WINDOW_CLOSING){
//            disconnectFromServer();
//        }
//        super.processWindowEvent(e);
//    }
}

