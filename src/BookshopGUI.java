package fairview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BookshopGUI extends JFrame {
    private BookshopManager manager;
    private JTabbedPane tabbedPane;
    
    // Stock panel components
    private JTable stockTable;
    private DefaultTableModel stockTableModel;
    
    // Orders panel components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    
    // Suppliers panel components
    private JTable suppliersTable;
    private DefaultTableModel suppliersTableModel;
    
    // Supplier Orders panel components
    private JTable supplierOrdersTable;
    private DefaultTableModel supplierOrdersTableModel;

    public BookshopGUI() {
        manager = new BookshopManager();
        
        setTitle("Bookshop Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Stock Management", createStockPanel());
        tabbedPane.addTab("Customer Orders", createOrdersPanel());
        tabbedPane.addTab("Suppliers", createSuppliersPanel());
        tabbedPane.addTab("Supplier Orders", createSupplierOrdersPanel());
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Authors", createAuthorsPanel());
        
        add(tabbedPane);
        
        // Add window listener to save data on close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                manager.saveData();
            }
        });
    }

    // Stock Panel
    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Stock Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ISBN", "Book Title", "Quantity", "Reorder Level", "Status", "Supplier"};
        stockTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(stockTableModel);
        JScrollPane scrollPane = new JScrollPane(stockTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshStockTable();
            }
        });
        
        JButton lowStockButton = new JButton("Show Low Stock");
        lowStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLowStock();
            }
        });
        
        JButton addStockButton = new JButton("Add Stock Item");
        addStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddStockDialog();
            }
        });
        
        JButton updateStockButton = new JButton("Update Quantity");
        updateStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStockQuantity();
            }
        });
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(lowStockButton);
        buttonsPanel.add(addStockButton);
        buttonsPanel.add(updateStockButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void refreshStockTable() {
        stockTableModel.setRowCount(0);
        ArrayList<Stock> stockList = manager.getAllStock();
        
        for (int i = 0; i < stockList.size(); i++) {
            Stock stock = stockList.get(i);
            Book book = manager.getBook(stock.getIsbn());
            Supplier supplier = manager.getSupplier(stock.getSupplierId());
            
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            String status = stock.needsReorder() ? "LOW STOCK" : "OK";
            String supplierName = supplier != null ? supplier.getName() : "Unknown";
            
            Object[] row = {
                stock.getIsbn(),
                bookTitle,
                stock.getQuantity(),
                stock.getReorderLevel(),
                status,
                supplierName
            };
            stockTableModel.addRow(row);
        }
    }

    private void showLowStock() {
        stockTableModel.setRowCount(0);
        ArrayList<Stock> lowStock = manager.getLowStock();
        
        for (int i = 0; i < lowStock.size(); i++) {
            Stock stock = lowStock.get(i);
            Book book = manager.getBook(stock.getIsbn());
            Supplier supplier = manager.getSupplier(stock.getSupplierId());
            
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            String supplierName = supplier != null ? supplier.getName() : "Unknown";
            
            Object[] row = {
                stock.getIsbn(),
                bookTitle,
                stock.getQuantity(),
                stock.getReorderLevel(),
                "LOW STOCK",
                supplierName
            };
            stockTableModel.addRow(row);
        }
    }

    private void showAddStockDialog() {
        JDialog dialog = new JDialog(this, "Add Stock Item", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Book:"));
        JComboBox<Book> bookCombo = new JComboBox<Book>();
        ArrayList<Book> books = manager.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            bookCombo.addItem(books.get(i));
        }
        panel.add(bookCombo);
        
        panel.add(new JLabel("Initial Quantity:"));
        JTextField quantityField = new JTextField();
        panel.add(quantityField);
        
        panel.add(new JLabel("Reorder Level:"));
        JTextField reorderField = new JTextField();
        panel.add(reorderField);
        
        panel.add(new JLabel("Supplier:"));
        JComboBox<Supplier> supplierCombo = new JComboBox<Supplier>();
        ArrayList<Supplier> suppliers = manager.getAllSuppliers();
        for (int i = 0; i < suppliers.size(); i++) {
            supplierCombo.addItem(suppliers.get(i));
        }
        panel.add(supplierCombo);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Book selectedBook = (Book) bookCombo.getSelectedItem();
                    Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
                    
                    if (selectedBook == null || selectedSupplier == null) {
                        JOptionPane.showMessageDialog(dialog, "Please select a book and supplier");
                        return;
                    }
                    
                    int quantity = Integer.parseInt(quantityField.getText());
                    int reorderLevel = Integer.parseInt(reorderField.getText());
                    
                    Stock stock = new Stock(selectedBook.getIsbn(), quantity, reorderLevel, selectedSupplier.getSupplierId());
                    manager.addStock(stock);
                    
                    refreshStockTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(BookshopGUI.this, "Stock item added successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numbers");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateStockQuantity() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock item to update");
            return;
        }
        
        String isbn = (String) stockTableModel.getValueAt(selectedRow, 0);
        String currentQty = stockTableModel.getValueAt(selectedRow, 2).toString();
        
        String newQtyStr = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQty);
        if (newQtyStr != null) {
            try {
                int newQty = Integer.parseInt(newQtyStr);
                manager.updateStock(isbn, newQty);
                refreshStockTable();
                JOptionPane.showMessageDialog(this, "Stock updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            }
        }
    }

    // Orders Panel
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Customer Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Order ID", "ISBN", "Book Title", "Quantity", "Customer", "Date", "Status"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshOrdersTable();
            }
        });
        
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPlaceOrderDialog();
            }
        });
        
        JButton completeButton = new JButton("Complete Order");
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                completeSelectedOrder();
            }
        });
        
        JButton pendingButton = new JButton("Show Pending");
        pendingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPendingOrders();
            }
        });
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(placeOrderButton);
        buttonsPanel.add(completeButton);
        buttonsPanel.add(pendingButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void refreshOrdersTable() {
        ordersTableModel.setRowCount(0);
        ArrayList<Order> orders = manager.getAllOrders();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Book book = manager.getBook(order.getIsbn());
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            
            Object[] row = {
                order.getOrderId(),
                order.getIsbn(),
                bookTitle,
                order.getQuantity(),
                order.getCustomerName(),
                order.getOrderDate(),
                order.getStatus()
            };
            ordersTableModel.addRow(row);
        }
    }

    private void showPendingOrders() {
        ordersTableModel.setRowCount(0);
        ArrayList<Order> orders = manager.getPendingOrders();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Book book = manager.getBook(order.getIsbn());
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            
            Object[] row = {
                order.getOrderId(),
                order.getIsbn(),
                bookTitle,
                order.getQuantity(),
                order.getCustomerName(),
                order.getOrderDate(),
                order.getStatus()
            };
            ordersTableModel.addRow(row);
        }
    }

    private void showPlaceOrderDialog() {
        JDialog dialog = new JDialog(this, "Place Order", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Book:"));
        JComboBox<Book> bookCombo = new JComboBox<Book>();
        ArrayList<Book> books = manager.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            bookCombo.addItem(books.get(i));
        }
        panel.add(bookCombo);
        
        panel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField();
        panel.add(quantityField);
        
        panel.add(new JLabel("Customer Name:"));
        JTextField customerField = new JTextField();
        panel.add(customerField);
        
        JButton placeButton = new JButton("Place Order");
        JButton cancelButton = new JButton("Cancel");
        
        placeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Book selectedBook = (Book) bookCombo.getSelectedItem();
                    if (selectedBook == null) {
                        JOptionPane.showMessageDialog(dialog, "Please select a book");
                        return;
                    }
                    
                    int quantity = Integer.parseInt(quantityField.getText());
                    String customer = customerField.getText();
                    
                    if (customer.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please enter customer name");
                        return;
                    }
                    
                    boolean success = manager.placeOrder(selectedBook.getIsbn(), quantity, customer);
                    if (success) {
                        refreshOrdersTable();
                        refreshStockTable();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(BookshopGUI.this, "Order placed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Insufficient stock for this order");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(placeButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void completeSelectedOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to complete");
            return;
        }
        
        String orderId = (String) ordersTableModel.getValueAt(selectedRow, 0);
        String status = (String) ordersTableModel.getValueAt(selectedRow, 6);
        
        if (status.equals("COMPLETED")) {
            JOptionPane.showMessageDialog(this, "This order is already completed");
            return;
        }
        
        manager.completeOrder(orderId);
        refreshOrdersTable();
        JOptionPane.showMessageDialog(this, "Order completed successfully!");
    }

    // Suppliers Panel
    private JPanel createSuppliersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Suppliers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Supplier ID", "Name", "Contact Person", "Phone", "Email"};
        suppliersTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        suppliersTable = new JTable(suppliersTableModel);
        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshSuppliersTable();
            }
        });
        
        JButton addButton = new JButton("Add Supplier");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddSupplierDialog();
            }
        });
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(addButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void refreshSuppliersTable() {
        suppliersTableModel.setRowCount(0);
        ArrayList<Supplier> suppliers = manager.getAllSuppliers();
        
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier supplier = suppliers.get(i);
            Object[] row = {
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getPhone(),
                supplier.getEmail()
            };
            suppliersTableModel.addRow(row);
        }
    }

    private void showAddSupplierDialog() {
        JDialog dialog = new JDialog(this, "Add Supplier", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Contact Person:"));
        JTextField contactField = new JTextField();
        panel.add(contactField);
        
        panel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField();
        panel.add(phoneField);
        
        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String contact = contactField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter supplier name");
                    return;
                }
                
                String supplierId = manager.generateSupplierId();
                Supplier supplier = new Supplier(supplierId, name, contact, phone, email);
                manager.addSupplier(supplier);
                
                refreshSuppliersTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(BookshopGUI.this, "Supplier added successfully!");
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Supplier Orders Panel
    private JPanel createSupplierOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Supplier Orders (Restocking)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Order ID", "Supplier", "ISBN", "Book Title", "Quantity", "Date", "Status"};
        supplierOrdersTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        supplierOrdersTable = new JTable(supplierOrdersTableModel);
        JScrollPane scrollPane = new JScrollPane(supplierOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshSupplierOrdersTable();
            }
        });
        
        JButton createOrderButton = new JButton("Order from Supplier");
        createOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCreateSupplierOrderDialog();
            }
        });
        
        JButton receiveButton = new JButton("Receive Order");
        receiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                receiveSelectedSupplierOrder();
            }
        });
        
        JButton pendingButton = new JButton("Show Pending");
        pendingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPendingSupplierOrders();
            }
        });
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(createOrderButton);
        buttonsPanel.add(receiveButton);
        buttonsPanel.add(pendingButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void refreshSupplierOrdersTable() {
        supplierOrdersTableModel.setRowCount(0);
        ArrayList<SupplierOrder> orders = manager.getAllSupplierOrders();
        
        for (int i = 0; i < orders.size(); i++) {
            SupplierOrder order = orders.get(i);
            Supplier supplier = manager.getSupplier(order.getSupplierId());
            Book book = manager.getBook(order.getIsbn());
            
            String supplierName = supplier != null ? supplier.getName() : "Unknown";
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            
            Object[] row = {
                order.getSupplierOrderId(),
                supplierName,
                order.getIsbn(),
                bookTitle,
                order.getQuantity(),
                order.getOrderDate(),
                order.getStatus()
            };
            supplierOrdersTableModel.addRow(row);
        }
    }

    private void showPendingSupplierOrders() {
        supplierOrdersTableModel.setRowCount(0);
        ArrayList<SupplierOrder> orders = manager.getPendingSupplierOrders();
        
        for (int i = 0; i < orders.size(); i++) {
            SupplierOrder order = orders.get(i);
            Supplier supplier = manager.getSupplier(order.getSupplierId());
            Book book = manager.getBook(order.getIsbn());
            
            String supplierName = supplier != null ? supplier.getName() : "Unknown";
            String bookTitle = book != null ? book.getTitle() : "Unknown";
            
            Object[] row = {
                order.getSupplierOrderId(),
                supplierName,
                order.getIsbn(),
                bookTitle,
                order.getQuantity(),
                order.getOrderDate(),
                order.getStatus()
            };
            supplierOrdersTableModel.addRow(row);
        }
    }

    private void showCreateSupplierOrderDialog() {
        JDialog dialog = new JDialog(this, "Order from Supplier", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Supplier:"));
        JComboBox<Supplier> supplierCombo = new JComboBox<Supplier>();
        ArrayList<Supplier> suppliers = manager.getAllSuppliers();
        for (int i = 0; i < suppliers.size(); i++) {
            supplierCombo.addItem(suppliers.get(i));
        }
        panel.add(supplierCombo);
        
        panel.add(new JLabel("Book:"));
        JComboBox<Book> bookCombo = new JComboBox<Book>();
        ArrayList<Book> books = manager.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            bookCombo.addItem(books.get(i));
        }
        panel.add(bookCombo);
        
        panel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField();
        panel.add(quantityField);
        
        JButton orderButton = new JButton("Send Order");
        JButton cancelButton = new JButton("Cancel");
        
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
                    Book selectedBook = (Book) bookCombo.getSelectedItem();
                    
                    if (selectedSupplier == null || selectedBook == null) {
                        JOptionPane.showMessageDialog(dialog, "Please select supplier and book");
                        return;
                    }
                    
                    int quantity = Integer.parseInt(quantityField.getText());
                    
                    manager.createSupplierOrder(selectedSupplier.getSupplierId(), selectedBook.getIsbn(), quantity);
                    
                    refreshSupplierOrdersTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(BookshopGUI.this, 
                        "Order sent to " + selectedSupplier.getName() + " for " + quantity + " copies of " + selectedBook.getTitle());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(orderButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void receiveSelectedSupplierOrder() {
        int selectedRow = supplierOrdersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier order to receive");
            return;
        }
        
        String orderId = (String) supplierOrdersTableModel.getValueAt(selectedRow, 0);
        String status = (String) supplierOrdersTableModel.getValueAt(selectedRow, 6);
        
        if (status.equals("RECEIVED")) {
            JOptionPane.showMessageDialog(this, "This order has already been received");
            return;
        }
        
        manager.receiveSupplierOrder(orderId);
        refreshSupplierOrdersTable();
        refreshStockTable();
        JOptionPane.showMessageDialog(this, "Supplier order received and stock updated!");
    }

    // Books Panel
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Books Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"ISBN", "Title", "Authors", "Price", "Category"};
        DefaultTableModel booksTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable booksTable = new JTable(booksTableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load books data
        ArrayList<Book> books = manager.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            StringBuilder authorsStr = new StringBuilder();
            ArrayList<Author> authors = book.getAuthors();
            for (int j = 0; j < authors.size(); j++) {
                authorsStr.append(authors.get(j).getName());
                if (j < authors.size() - 1) {
                    authorsStr.append(", ");
                }
            }
            
            Object[] row = {
                book.getIsbn(),
                book.getTitle(),
                authorsStr.toString(),
                "£" + book.getPrice(),
                book.getCategory()
            };
            booksTableModel.addRow(row);
        }
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddBookDialog(booksTableModel);
            }
        });
        
        buttonsPanel.add(addButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void showAddBookDialog(final DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Add Book", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("ISBN:"));
        JTextField isbnField = new JTextField();
        panel.add(isbnField);
        
        panel.add(new JLabel("Title:"));
        JTextField titleField = new JTextField();
        panel.add(titleField);
        
        panel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        panel.add(priceField);
        
        panel.add(new JLabel("Category:"));
        JTextField categoryField = new JTextField();
        panel.add(categoryField);
        
        panel.add(new JLabel("Authors:"));
        JList<Author> authorsList = new JList<Author>();
        ArrayList<Author> allAuthors = manager.getAllAuthors();
        Author[] authorsArray = new Author[allAuthors.size()];
        for (int i = 0; i < allAuthors.size(); i++) {
            authorsArray[i] = allAuthors.get(i);
        }
        authorsList.setListData(authorsArray);
        authorsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane authorsScroll = new JScrollPane(authorsList);
        panel.add(authorsScroll);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String isbn = isbnField.getText();
                    String title = titleField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    String category = categoryField.getText();
                    
                    if (isbn.trim().isEmpty() || title.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "ISBN and Title are required");
                        return;
                    }
                    
                    Book book = new Book(isbn, title, price, category);
                    
                    int[] selectedIndices = authorsList.getSelectedIndices();
                    for (int i = 0; i < selectedIndices.length; i++) {
                        book.addAuthor(authorsArray[selectedIndices[i]]);
                    }
                    
                    manager.addBook(book);
                    
                    // Add to table
                    StringBuilder authorsStr = new StringBuilder();
                    ArrayList<Author> authors = book.getAuthors();
                    for (int i = 0; i < authors.size(); i++) {
                        authorsStr.append(authors.get(i).getName());
                        if (i < authors.size() - 1) {
                            authorsStr.append(", ");
                        }
                    }
                    
                    Object[] row = {
                        book.getIsbn(),
                        book.getTitle(),
                        authorsStr.toString(),
                        "£" + book.getPrice(),
                        book.getCategory()
                    };
                    tableModel.addRow(row);
                    
                    dialog.dispose();
                    JOptionPane.showMessageDialog(BookshopGUI.this, "Book added successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid price");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Authors Panel
    private JPanel createAuthorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Authors");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Author ID", "Name", "Email"};
        DefaultTableModel authorsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable authorsTable = new JTable(authorsTableModel);
        JScrollPane scrollPane = new JScrollPane(authorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load authors data
        ArrayList<Author> authors = manager.getAllAuthors();
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            Object[] row = {
                author.getAuthorId(),
                author.getName(),
                author.getEmail()
            };
            authorsTableModel.addRow(row);
        }
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Author");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddAuthorDialog(authorsTableModel);
            }
        });
        
        buttonsPanel.add(addButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void showAddAuthorDialog(final DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Add Author", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name is required");
                    return;
                }
                
                String authorId = manager.generateAuthorId();
                Author author = new Author(authorId, name, email);
                manager.addAuthor(author);
                
                Object[] row = {
                    author.getAuthorId(),
                    author.getName(),
                    author.getEmail()
                };
                tableModel.addRow(row);
                
                dialog.dispose();
                JOptionPane.showMessageDialog(BookshopGUI.this, "Author added successfully!");
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadInitialData() {
        refreshStockTable();
        refreshOrdersTable();
        refreshSuppliersTable();
        refreshSupplierOrdersTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BookshopGUI().setVisible(true);
            }
        });
    }
}
