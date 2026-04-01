package model.entity;

public class CartItemEntity {
    private int id;
    private int cartRecordId;
    private int itemNumber;
    private String itemName;
    private double price;
    private int quantity;
    private double subtotal;

    public CartItemEntity   () {}

    public CartItemEntity(int cartRecordId, int itemNumber, String itemName, double price, int quantity) {
        this.cartRecordId = cartRecordId;
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartRecordId() { return cartRecordId; }
    public void setCartRecordId(int cartRecordId) { this.cartRecordId = cartRecordId; }

    public int getItemNumber() { return itemNumber; }
    public void setItemNumber(int itemNumber) { this.itemNumber = itemNumber; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}

