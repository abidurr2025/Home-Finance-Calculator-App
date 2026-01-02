import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// ===============================
// Finance Calculations (Core Logic)
// ===============================
class FinanceCalculations {
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static double calculateMonthlyPayment(double principal, double annualRate, int years) {
        if (principal <= 0 || years <= 0) {
            return 0;
        }
        double monthlyRate = annualRate / 100 / 12;
        int payments = years * 12;
        if (monthlyRate == 0) {
            return principal / payments;
        }
        return (principal * monthlyRate * Math.pow(1 + monthlyRate, payments)) /
                (Math.pow(1 + monthlyRate, payments) - 1);
    }

    public static List<Map<String, Double>> generateAmortizationSchedule(double loan, double rate, int years) {
        List<Map<String, Double>> schedule = new ArrayList<>();
        if (loan <= 0 || rate <= 0 || years <= 0) {
            return schedule;
        }

        double payment = calculateMonthlyPayment(loan, rate, years);
        double monthlyRate = rate / 100 / 12;
        double balance = loan;

        for (int i = 1; i <= years * 12; i++) {
            double interest = balance * monthlyRate;
            double principal = payment - interest;
            balance -= principal;
            balance = Math.max(balance, 0);

            Map<String, Double> row = new HashMap<>();
            row.put("payment", (double) i);
            row.put("payment_amount", payment);
            row.put("principal", principal);
            row.put("interest", interest);
            row.put("balance", balance);
            schedule.add(row);
        }

        return schedule;
    }

    public static Map<String, Double> compareProperty(double price, double down, double rate,
                                                      double rent, double expenses, int years) {
        Map<String, Double> result = new HashMap<>();
        double loan = price - down;
        double monthlyPayment = calculateMonthlyPayment(loan, rate, years);
        double monthlyCashFlow = rent - expenses - monthlyPayment;
        double annualCashFlow = monthlyCashFlow * 12;
        double investment = down + (price * 0.03); // 3% closing costs

        double coc = (investment > 0) ? (annualCashFlow / investment * 100) : 0;
        double capRate = (price > 0) ? ((rent * 12 - expenses * 12) / price * 100) : 0;

        result.put("monthly_payment", monthlyPayment);
        result.put("monthly_cash_flow", monthlyCashFlow);
        result.put("coc", coc);
        result.put("cap_rate", capRate);
        result.put("investment", investment);

        return result;
    }
}

// ===============================
// Custom Input Panel
// ===============================
class CleanInput extends JPanel {
    private JTextField inputField;

    public CleanInput(String label, String defaultValue) {
        setLayout(new BorderLayout(5, 0));
        setPreferredSize(new Dimension(400, 40));

        JLabel titleLabel = new JLabel(label);
        titleLabel.setPreferredSize(new Dimension(150, 30));
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        inputField = new JTextField(defaultValue);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));

        add(titleLabel, BorderLayout.WEST);
        add(inputField, BorderLayout.CENTER);
    }

    public double getValue() {
        try {
            return Double.parseDouble(inputField.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

// ===============================
// Mortgage Tab
// ===============================
class MortgageTab extends JPanel {
    private CleanInput priceInput, downInput, rateInput, yearsInput;
    private JLabel resultLabel;

    public MortgageTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Mortgage Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Inputs
        priceInput = new CleanInput("Home Price ($):", "300000");
        downInput = new CleanInput("Down Payment ($):", "60000");
        rateInput = new CleanInput("Interest Rate (%):", "4.5");
        yearsInput = new CleanInput("Loan Term (years):", "30");

        add(priceInput);
        add(Box.createVerticalStrut(10));
        add(downInput);
        add(Box.createVerticalStrut(10));
        add(rateInput);
        add(Box.createVerticalStrut(10));
        add(yearsInput);
        add(Box.createVerticalStrut(20));

        // Calculate Button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setPreferredSize(new Dimension(200, 40));
        calculateButton.addActionListener(e -> calculate());

        add(calculateButton);
        add(Box.createVerticalStrut(20));

        // Result
        resultLabel = new JLabel("Monthly Payment: $0.00");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(new Color(0, 100, 0));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(resultLabel);
    }

    private void calculate() {
        double loan = priceInput.getValue() - downInput.getValue();
        double payment = FinanceCalculations.calculateMonthlyPayment(
                loan, rateInput.getValue(), (int) yearsInput.getValue()
        );

        DecimalFormat df = new DecimalFormat("#,##0.00");
        resultLabel.setText("Monthly Payment: $" + df.format(payment));
    }
}

// ===============================
// Affordability Tab
// ===============================
class AffordabilityTab extends JPanel {
    private CleanInput incomeInput, debtsInput, downPctInput, rateInput, yearsInput;
    private JTextArea resultArea;

    public AffordabilityTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Affordability Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Inputs
        incomeInput = new CleanInput("Annual Income ($):", "75000");
        debtsInput = new CleanInput("Monthly Debts ($):", "500");
        downPctInput = new CleanInput("Down Payment (%):", "20");
        rateInput = new CleanInput("Interest Rate (%):", "4.5");
        yearsInput = new CleanInput("Loan Term (years):", "30");

        add(incomeInput);
        add(Box.createVerticalStrut(10));
        add(debtsInput);
        add(Box.createVerticalStrut(10));
        add(downPctInput);
        add(Box.createVerticalStrut(10));
        add(rateInput);
        add(Box.createVerticalStrut(10));
        add(yearsInput);
        add(Box.createVerticalStrut(20));

        // Calculate Button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setPreferredSize(new Dimension(200, 40));
        calculateButton.addActionListener(e -> calculate());

        add(calculateButton);
        add(Box.createVerticalStrut(20));

        // Result Area
        resultArea = new JTextArea(5, 30);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(scrollPane);
    }

    private void calculate() {
        double monthlyIncome = incomeInput.getValue() / 12;
        double maxPayment = monthlyIncome * 0.36 - debtsInput.getValue();

        if (maxPayment <= 0) {
            resultArea.setText("Not affordable with current debts.");
            return;
        }

        double rate = rateInput.getValue();
        int years = (int) yearsInput.getValue();
        double downPct = downPctInput.getValue() / 100;

        double monthlyRate = rate / 100 / 12;
        int payments = years * 12;

        double loan;
        if (monthlyRate == 0) {
            loan = maxPayment * payments;
        } else {
            loan = maxPayment / (
                    (monthlyRate * Math.pow(1 + monthlyRate, payments)) /
                            (Math.pow(1 + monthlyRate, payments) - 1)
            );
        }

        double price = loan / (1 - downPct);
        double down = price * downPct;

        DecimalFormat df = new DecimalFormat("#,##0");
        resultArea.setText(
                "Affordable Home Price: $" + df.format(price) + "\n" +
                        "Down Payment: $" + df.format(down) + "\n" +
                        "Loan Amount: $" + df.format(loan) + "\n" +
                        "Max Monthly Payment: $" + String.format("%.2f", maxPayment)
        );
    }
}

// ===============================
// Rental ROI Tab
// ===============================
class RentalTab extends JPanel {
    private CleanInput priceInput, downInput, rateInput, rentInput, expInput;
    private JLabel resultLabel;

    public RentalTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Rental ROI Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Inputs
        priceInput = new CleanInput("Purchase Price ($):", "300000");
        downInput = new CleanInput("Down Payment ($):", "75000");
        rateInput = new CleanInput("Interest Rate (%):", "5");
        rentInput = new CleanInput("Monthly Rent ($):", "2000");
        expInput = new CleanInput("Monthly Expenses ($):", "400");

        add(priceInput);
        add(Box.createVerticalStrut(10));
        add(downInput);
        add(Box.createVerticalStrut(10));
        add(rateInput);
        add(Box.createVerticalStrut(10));
        add(rentInput);
        add(Box.createVerticalStrut(10));
        add(expInput);
        add(Box.createVerticalStrut(20));

        // Calculate Button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setPreferredSize(new Dimension(200, 40));
        calculateButton.addActionListener(e -> calculate());

        add(calculateButton);
        add(Box.createVerticalStrut(20));

        // Result
        resultLabel = new JLabel("Cash Flow: $0.00");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(new Color(0, 100, 0));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(resultLabel);
    }

    private void calculate() {
        Map<String, Double> data = FinanceCalculations.compareProperty(
                priceInput.getValue(),
                downInput.getValue(),
                rateInput.getValue(),
                rentInput.getValue(),
                expInput.getValue(),
                30
        );

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String result = String.format(
                "<html><center>" +
                        "Monthly Cash Flow: $%s<br>" +
                        "Cash-on-Cash Return: %.2f%%<br>" +
                        "Capitalization Rate: %.2f%%<br>" +
                        "Total Investment: $%s" +
                        "</center></html>",
                df.format(data.get("monthly_cash_flow")),
                data.get("coc"),
                data.get("cap_rate"),
                df.format(data.get("investment"))
        );

        resultLabel.setText(result);
    }
}

// ===============================
// Compare Tab
// ===============================
class CompareTab extends JPanel {
    private CleanInput p1Price, p1Down, p1Rate, p1Rent, p1Exp;
    private CleanInput p2Price, p2Down, p2Rate, p2Rent, p2Exp;
    private JLabel resultLabel;

    public CompareTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Property Comparison");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Property 1 Inputs
        JLabel p1Label = new JLabel("Property 1");
        p1Label.setFont(new Font("Arial", Font.BOLD, 14));
        p1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(p1Label);
        add(Box.createVerticalStrut(10));

        p1Price = new CleanInput("Price:", "300000");
        p1Down = new CleanInput("Down:", "60000");
        p1Rate = new CleanInput("Rate %:", "4.5");
        p1Rent = new CleanInput("Rent:", "2000");
        p1Exp = new CleanInput("Expenses:", "500");

        add(p1Price);
        add(Box.createVerticalStrut(5));
        add(p1Down);
        add(Box.createVerticalStrut(5));
        add(p1Rate);
        add(Box.createVerticalStrut(5));
        add(p1Rent);
        add(Box.createVerticalStrut(5));
        add(p1Exp);
        add(Box.createVerticalStrut(20));

        // Property 2 Inputs
        JLabel p2Label = new JLabel("Property 2");
        p2Label.setFont(new Font("Arial", Font.BOLD, 14));
        p2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(p2Label);
        add(Box.createVerticalStrut(10));

        p2Price = new CleanInput("Price:", "250000");
        p2Down = new CleanInput("Down:", "50000");
        p2Rate = new CleanInput("Rate %:", "4.5");
        p2Rent = new CleanInput("Rent:", "1800");
        p2Exp = new CleanInput("Expenses:", "450");

        add(p2Price);
        add(Box.createVerticalStrut(5));
        add(p2Down);
        add(Box.createVerticalStrut(5));
        add(p2Rate);
        add(Box.createVerticalStrut(5));
        add(p2Rent);
        add(Box.createVerticalStrut(5));
        add(p2Exp);
        add(Box.createVerticalStrut(20));

        // Compare Button
        JButton compareButton = new JButton("Compare Properties");
        compareButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        compareButton.setFont(new Font("Arial", Font.BOLD, 14));
        compareButton.setPreferredSize(new Dimension(200, 40));
        compareButton.addActionListener(e -> compare());

        add(compareButton);
        add(Box.createVerticalStrut(20));

        // Result
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(resultLabel);
    }

    private void compare() {
        Map<String, Double> p1 = FinanceCalculations.compareProperty(
                p1Price.getValue(), p1Down.getValue(),
                p1Rate.getValue(), p1Rent.getValue(), p1Exp.getValue(), 30
        );

        Map<String, Double> p2 = FinanceCalculations.compareProperty(
                p2Price.getValue(), p2Down.getValue(),
                p2Rate.getValue(), p2Rent.getValue(), p2Exp.getValue(), 30
        );

        String winner = p1.get("coc") > p2.get("coc") ? "Property 1" : "Property 2";

        String result = String.format(
                "<html><center>" +
                        "Property 1 CoC: %.2f%%<br>" +
                        "Property 2 CoC: %.2f%%<br><br>" +
                        "<font color='blue' size='+1'><b>Winner: %s</b></font>" +
                        "</center></html>",
                p1.get("coc"), p2.get("coc"), winner
        );

        resultLabel.setText(result);
    }
}

// ===============================
// Amortization Tab
// ===============================
class AmortizationTab extends JPanel {
    private CleanInput loanInput, rateInput, yearsInput;
    private JTextArea scheduleArea;
    private List<Map<String, Double>> schedule;

    public AmortizationTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Amortization Schedule");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Inputs
        loanInput = new CleanInput("Loan Amount ($):", "240000");
        rateInput = new CleanInput("Interest Rate (%):", "4.5");
        yearsInput = new CleanInput("Loan Term (years):", "30");

        add(loanInput);
        add(Box.createVerticalStrut(10));
        add(rateInput);
        add(Box.createVerticalStrut(10));
        add(yearsInput);
        add(Box.createVerticalStrut(20));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton generateButton = new JButton("Generate Schedule");
        JButton exportButton = new JButton("Export to CSV");

        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));

        generateButton.addActionListener(e -> generateSchedule());
        exportButton.addActionListener(e -> exportToCSV());

        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(buttonPanel);
        add(Box.createVerticalStrut(20));

        // Schedule Area
        scheduleArea = new JTextArea(15, 40);
        scheduleArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scheduleArea.setEditable(false);
        scheduleArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Amortization Schedule (First 24 payments)"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(scrollPane);
    }

    private void generateSchedule() {
        schedule = FinanceCalculations.generateAmortizationSchedule(
                loanInput.getValue(), rateInput.getValue(), (int) yearsInput.getValue()
        );

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-12s %-12s %-12s %-12s\n",
                "Month", "Payment", "Principal", "Interest", "Balance"));
        sb.append("=".repeat(60)).append("\n");

        int limit = Math.min(24, schedule.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Double> row = schedule.get(i);
            sb.append(String.format("%-6d %-12.2f %-12.2f %-12.2f %-12.2f\n",
                    row.get("payment").intValue(),
                    row.get("payment_amount"),
                    row.get("principal"),
                    row.get("interest"),
                    row.get("balance")
            ));
        }

        scheduleArea.setText(sb.toString());
    }

    private void exportToCSV() {
        if (schedule == null || schedule.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please generate schedule first!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = "amort_" + sdf.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(fileName));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("#,Payment,Principal,Interest,Balance");
                for (Map<String, Double> row : schedule) {
                    writer.printf("%d,%.2f,%.2f,%.2f,%.2f\n",
                            row.get("payment").intValue(),
                            row.get("payment_amount"),
                            row.get("principal"),
                            row.get("interest"),
                            row.get("balance")
                    );
                }
                JOptionPane.showMessageDialog(this,
                        "Schedule exported successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting file: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// ===============================
// Main Application
// ===============================
class HousingFinanceApp extends JFrame {

    public HousingFinanceApp() {
        setTitle("Real Estate Finance Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setMinimumSize(new Dimension(500, 600));

        // Center the window
        setLocationRelativeTo(null);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Mortgage", new MortgageTab());
        tabbedPane.addTab("Affordability", new AffordabilityTab());
        tabbedPane.addTab("ROI", new RentalTab());
        tabbedPane.addTab("Compare", new CompareTab());
        tabbedPane.addTab("Schedule", new AmortizationTab());

        // Set tab font
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        add(tabbedPane);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Real Estate Finance Calculator\n\n" +
                            "Version 1.0\n" +
                            "Provides mortgage, affordability, ROI,\n" +
                            "property comparison, and amortization tools.",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HousingFinanceApp app = new HousingFinanceApp();
            app.setVisible(true);
        });
    }
}