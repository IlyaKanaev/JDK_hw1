import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    public static final int WIDTH = 400; // размеры основного (серверного) окна
    public static final int HEIGHT = 300;
    public static final String LOG_PATH = "src/server/log.txt"; // путь к файлу с логами (но у меня он не создался...)

    List<ClientGUI> clientGUIList; // ???

    JButton btnStart, btnStop; // кнопки запуска и остановки чата
    JTextArea log; // область введения текста в окне сервера
    boolean work; // ???

    public ServerWindow(){
        clientGUIList = new ArrayList<>(); // создаем список для подключаемых клиентов (но у нас их в итоге будет 2)

        setDefaultCloseOperation(EXIT_ON_CLOSE); // помечаем это окно, как основное
        setSize(WIDTH, HEIGHT);
        // setResizable(false); // а вот не надо излишеств. Больше свободы пользователям!
        setTitle("Chat server");
        setLocationRelativeTo(null); // без этого клиентские окна видны не будут, хотя вроде бы это только центрирование...

        createPanel(); // создаем общую панель

        setVisible(true);
    }

    // ???
    public boolean connectUser(ClientGUI clientGUI){
        if (!work){
            return false;
        }
        clientGUIList.add(clientGUI);
        return true;
    }

    // некая прокладка перед приватной readLog() Нужна ли она?.. наверное для ограничения доступа к логам
    public String getLog() {
        return readLog();
    }

    // взаимосвязанный с клиентским аналогом метод отключения
    public void disconnectUser(ClientGUI clientGUI){
        clientGUIList.remove(clientGUI);
        if (clientGUI != null)
            clientGUI.disconnectFromServer();
    }

    // метод логгирования
    public void message(String text){
        if (!work) return;
        // text += ""; // вот зачем это? и без него вроде работает
        appendLog(text); // добавляем в текст чата в серверном окне
        answerAll(text); // попугайничаем всем подключенным
        saveInLog(text); // сохраняем наш бред в файл
    }

    // метод рассылки всех сообщений всем подключенным
    private void answerAll(String text){
        for (ClientGUI clientGUI: clientGUIList)
            clientGUI.answer(text);
    }

    private void saveInLog(String text){
        try (FileWriter writer = new FileWriter(LOG_PATH, true)){
            writer.write(text);
            writer.write("\n");
        } catch (Exception e){
            System.out.println("Сохраняем");//e.printStackTrace(); // вот из-за такой фигни в терминале все красное и страшное
        }
    }

    private String readLog(){
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH);){
            int c;
            while ((c = reader.read()) != -1){
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e){
            System.out.println("Читаем");//e.printStackTrace();
            return null;
        }
    }

    // мусорим наши логи в файл
    private void appendLog(String text){
        log.append(text + "\n");
    }

    // создание общей панели сервера из области чата (log) и панели кнопок (createButtons())
    private void createPanel() {
        log = new JTextArea();
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }

    // панель для кнопок Start и Stop в окошке сервера
    private Component createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");

        // два обработчика для двух кнопок
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (work){
                    appendLog("Сервер уже был запущен");
                } else {
                    work = true;
                    appendLog("Сервер запущен!");
                }
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!work){
                    appendLog("Сервер уже был остановлен");
                } else {
                    work = false;
                    while (!clientGUIList.isEmpty()){
                        disconnectUser(clientGUIList.get(clientGUIList.size()-1));
                    }
                    appendLog("Сервер остановлен!");
                }
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }
}

