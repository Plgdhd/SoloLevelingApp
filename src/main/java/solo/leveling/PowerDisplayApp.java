package solo.leveling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class PowerDisplayApp extends JPanel {
    private double[] values = { 20, 20, 20, 20, 20, 20 };
    private final String[] labels = { "Physical", "Algorithms", "Java", "DevOps", "Frontend", "Mindset" };
    private static final String FILE_NAME = "stats.txt";

    public PowerDisplayApp() {
        loadValues();
    }

    public void setValues(double[] newValues) {
        this.values = newValues;
        repaint();
    }

    private String getRank() {
        double avg = 0;
        for (double v : values) {
            avg += v;
        }
        avg /= values.length;
        if (avg >= 90)
            return "S";
        if (avg >= 75)
            return "A";
        if (avg >= 60)
            return "B";
        if (avg >= 40)
            return "C";
        if (avg >= 20)
            return "D";
        return "E";
    }

    private void saveValues() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (double value : values) {
                writer.write(value + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadValues() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] data = reader.readLine().split(" ");
                for (int i = 0; i < values.length; i++) {
                    values[i] = Double.parseDouble(data[i]);
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3;
        for (int i = 0; i < labels.length; i++) {
            double angle = Math.PI / 3 * i - Math.PI / 2;
            int x = centerX + (int) (Math.cos(angle) * radius);
            int y = centerY + (int) (Math.sin(angle) * radius);
            g2.drawLine(centerX, centerY, x, y);
            g2.drawString(labels[i] + " (" + (int) values[i] + ")", x, y);
        }

        Polygon polygon = new Polygon();
        for (int i = 0; i < values.length; i++) {
            double angle = Math.PI / 3 * i - Math.PI / 2;
            int x = centerX + (int) (Math.cos(angle) * values[i] / 100 * radius);
            int y = centerY + (int) (Math.sin(angle) * values[i] / 100 * radius);
            polygon.addPoint(x, y);
        }

        g2.setColor(new Color(25, 116, 210, 100));
        g2.fillPolygon(polygon);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(polygon);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Your current stats: ", centerX - 40, centerY - 200);
        g2.drawString("Rank: " + getRank(), centerX - 30, centerY + radius + 70);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Way to middle");
        PowerDisplayApp chart = new PowerDisplayApp();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 750);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 3));
        JButton[] buttons = new JButton[6];
        
        for (int i = 0; i < 6; i++) {
            final int index = i;
            buttons[i] = new JButton(chart.labels[i]);
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chart.values[index] < 100) {
                        chart.values[index] += 0.5;
                        chart.repaint();
                        chart.saveValues();
                    }
                }
            });
            controlPanel.add(buttons[i]);
        }
        
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("?");
        JMenuItem infoItem = new JMenuItem("Info");
        infoItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
  "Physical - workouts, exercise, sports\n" +
                    "(100 push-ups and similar - 0.5 points, strength training in the gym + 1 point)\n\n" +
                    "Algorithms - solving algorithm problems or studying them\n" +
                    "(1 hour of studying: 0.5 points, one problem on LeetCode + 1 point)\n\n" +
                    "Java - practicing and learning the language\n" +
                    "(2 hours of study + 0.5, 3 hours of working on any project + 0.5)\n\n" +
                    "DevOps - working with CI/CD, Docker, Kubernetes\n" +
                    "(2 hours of study + 0.5 points)\n\n" +
                    "Frontend - layout, JS, React, etc.\n" +
                    "(2 hours of study + 0.5 points, 3 hours of working on any project + 0.5 points)\n\n" +
                    "Mindset - read or watch 20 pages/20 minutes of useful information + 0.5 points" ));
        helpMenu.add(infoItem);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);
        
        frame.setLayout(new BorderLayout());
        frame.add(chart, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                chart.saveValues();
            }
        });
        frame.setVisible(true);
    }
}