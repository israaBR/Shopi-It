package com.example.shop_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class databaseHelper extends SQLiteOpenHelper {
    private static final String dbName = "shopitDB";
    static SQLiteDatabase shopit;
    public databaseHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists customer(customer_id integer primary key autoincrement, customer_name text not null, customer_email text not null, customer_password text not null, customer_mobile_number text, customer_birth_date text);");
        db.execSQL("create table if not exists category(category_id integer primary key autoincrement, category_name text not null, category_image blob)");
        db.execSQL("create table if not exists product(product_id integer primary key autoincrement, product_name text not null, product_price float not null, product_quantity integer not null, product_image blob not null,product_category_id integer references category(category_id));");
        db.execSQL("create table if not exists customer_cart(cart_id integer primary key autoincrement, order_total_price float, order_address text, payment_method text, credit_card_number text, cart_customer_id integer references customer(customer_id));");
        db.execSQL("create table if not exists cart_product(cart_id_fk integer references customer_cart(cart_id), product_id_fk integer references product(product_id), order_product_quantity integer, order_product_price float);");
        db.execSQL("create table if not exists recovery_question(recovery_question_id integer primary key autoincrement, recovery_question_txt text);");
        db.execSQL("create table if not exists customer_question(customer_id_fk integer references customer(customer_id), question_id integer references recovery_question(recovery_question_id), question_answer text);");
        fill_category_table(db);
        fill_product_table(db);
        fill_question_table(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists customer");
        db.execSQL("drop table if exists category");
        db.execSQL("drop table if exists product");
        db.execSQL("drop table if exists customer_cart");
        db.execSQL("drop table if exists cart_product");
        db.execSQL("drop table if exists recovery_question");
        db.execSQL("drop table if exists customer_question");

        onCreate(db);
    }


    /////////insert data in the database////////////
    public boolean add_customer(String name, String email, String password){
        shopit = getWritableDatabase();
        ContentValues customerRow = new ContentValues();
        boolean emailExist = get_customer_email(email);
        if(emailExist)
            return false;//user isn't added
        else {
            customerRow.put("customer_name", name);
            customerRow.put("customer_email", email);
            customerRow.put("customer_password", password);
            shopit.insert("customer", null, customerRow);
            shopit.close();
            return true; //user is added
        }
    }
    public void add_customer_cart(int customer_ID){
        shopit = getWritableDatabase();
        ContentValues cartRow = new ContentValues();
        cartRow.put("cart_customer_id", customer_ID);
        cartRow.put("order_total_price", 0);
        cartRow.put("order_address", " ");
        cartRow.put("payment_method", " ");

        shopit.insert("customer_cart", null, cartRow);
        shopit.close();
    }
    public void add_customer_security_questions(int customer_ID, int question1_ID, String question1_answer, int question2_ID, String question2_answer) {

        //first: check if customer has set these questions or not(insert or edit?)
        Cursor cursor = get_customer_questions(customer_ID);
        if(cursor != null) //questions exist
            delete_customer_security_questions(customer_ID); //delete old questions

        //second: insert new questions
        shopit = getWritableDatabase();
        int[] question = {question1_ID, question2_ID};
        String[] answer= {question1_answer, question2_answer};
        ContentValues questionRow = new ContentValues();
        for(int i=0; i<2; i++)
        {
            questionRow.put("question_id", question[i]);
            questionRow.put("question_answer", answer[i]);
            questionRow.put("customer_id_fk", customer_ID);
            shopit.insert("customer_question", null, questionRow);
            questionRow.clear();
        }
        shopit.close();
    }
    public void add_product_to_cart(int customer_ID, int product_ID, int product_quantity, float product_price){
        //get cart id using customer id
        Cursor cart = get_customer_cart(customer_ID);
        int cart_ID = cart.getInt(0);
        //get order old total price
        float order_total_price = cart.getFloat(1);
        //get product old quantity
        Cursor product = get_product_data(product_ID);
        int oldQuantity = product.getInt(3);
        //modify product quantity in product table(new quantity = old quantity - taken quantity)
        shopit = getWritableDatabase();
        ContentValues Row = new ContentValues();
        Row.put("product_quantity",oldQuantity-product_quantity);
        shopit.update("product", Row, "product_id like? ", new String[]{String.valueOf(product_ID)});
        //add new row to cart_product table with the cart id, product id, product taken quantity & total price(product price * taken quantity)
        float productprice = product_quantity * product_price;
        Row.clear();
        Row.put("cart_id_fk", cart_ID);
        Row.put("product_id_fk", product_ID);
        Row.put("order_product_quantity", product_quantity);
        Row.put("order_product_price", productprice);
        shopit.insert("cart_product", null, Row);
        //add product price to order total price in customer_cart table
        Row.clear();
        Row.put("order_total_price", order_total_price + productprice);
        shopit.update("customer_cart", Row, "cart_id like? ", new String[]{String.valueOf(cart_ID)});
        shopit.close();
    }
    

    /////////select data from the database////////////
    public boolean check_customer(String email, String password) {
        shopit = getReadableDatabase();
        String[] arg = {email, password};
        Cursor cursor = shopit.rawQuery("select * from customer where customer_email like ? and customer_password like ?", arg);
        if (cursor.getCount()>0)
            return true; //customer exists
        else
            return false; //customer doesn't exist
    }
    public boolean get_customer_email(String customer_email) {
        shopit = getReadableDatabase();
        Cursor cursor = shopit.rawQuery("select customer_email from customer", null);
        if (cursor != null)
            cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0).equals(customer_email))
                return true;//email exists
            else
                cursor.moveToNext();
        }
        return false; //email doesn't exist
    }//ممكن استعمل get customer data
    public int get_customer_id(String customer_email){
       shopit = getReadableDatabase();
       Cursor cursor = shopit.rawQuery("select customer_id, customer_email from customer", null);
       cursor.moveToFirst();
       while(!cursor.isAfterLast()){
           if(cursor.getString(1).equals(customer_email))
               return Integer.parseInt(cursor.getString(0));
           else
               cursor.moveToNext();
       }
       shopit.close();
        return  -1;
    }
    public String get_category_name(int category_ID){
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(category_ID)};
        Cursor cursor = shopit.rawQuery("select * from category where category_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        String categoryName = cursor.getString(1);
        shopit.close();
        return categoryName;
    }
    public Cursor get_security_questions() {
        shopit = getReadableDatabase();
        Cursor cursor = shopit.rawQuery("select * from recovery_question", null);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_customer_questions(int customer_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(customer_ID)};
        Cursor cursor = shopit.rawQuery("select * from customer_question where customer_id_fk like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_question(int question_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(question_ID)};
        Cursor cursor = shopit.rawQuery("select * from recovery_question where recovery_question_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;

    }
    public Cursor get_customer_data(int customer_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(customer_ID)};
        Cursor cursor = shopit.rawQuery("select * from customer where customer_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_categories() {
        shopit = getReadableDatabase();
        Cursor cursor = shopit.rawQuery("select * from category", null);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_product_data(int product_ID){
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(product_ID)};
        Cursor cursor = shopit.rawQuery("select * from product where product_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_products(int category_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(category_ID)};
        Cursor cursor = shopit.rawQuery("select * from product where product_category_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_cart_products(int cart_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(cart_ID)};
        Cursor cursor = shopit.rawQuery("select * from cart_product where cart_id_fk like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_customer_cart(int customer_ID) {
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(customer_ID)};
        Cursor cursor = shopit.rawQuery("select * from customer_cart where cart_customer_id like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor get_cart_product_data(int cart_ID, int product_ID){
        shopit = getReadableDatabase();
        String[] args = {String.valueOf(cart_ID), String.valueOf(product_ID)};
        Cursor cursor = shopit.rawQuery("select * from cart_product where cart_id_fk like ? and product_id_fk like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        shopit.close();
        return cursor;
    }
    public Cursor search_by_product(String product_name){
        shopit = getReadableDatabase();
        Cursor products = shopit.rawQuery("select * from product where product_name like ?", new String[]{"%" + product_name + "%"});
        products.moveToFirst();
        shopit.close();
        return products;
    }

    /////////update data in the database////////////
    public void update_personalInfo(int customer_ID, String name, String email, String mobileNumber, String birthDate) {
        shopit = getWritableDatabase();
        ContentValues customer = new ContentValues();
        customer.put("customer_name", name);
        customer.put("customer_email", email);
        customer.put("customer_mobile_number", mobileNumber);
        customer.put("customer_birth_date", birthDate);
        String[] arg = {String.valueOf(customer_ID)};
        shopit.update("customer", customer, "customer_id like ?", arg);
        shopit.close();
    }
    public void update_cart_payment_method(int cart_ID, String paymentMethod, String creditcardNumber) {
        shopit = getWritableDatabase();
        ContentValues cart = new ContentValues();
        cart.put("payment_method", paymentMethod);
        if(paymentMethod.equals("credit card"))
            cart.put(" credit_card_number", creditcardNumber);
        String[] arg = {String.valueOf(cart_ID)};
        shopit.update("customer_cart", cart, "cart_id like ?", arg);
        shopit.close();
    }
    public void update_cart_delivery_address(int cart_ID, String deliveryAddress){
        shopit = getWritableDatabase();
        ContentValues cart = new ContentValues();
        cart.put("order_address", deliveryAddress);
        String[] arg = {String.valueOf(cart_ID)};
        shopit.update("customer_cart", cart, "cart_id like ?", arg);
        shopit.close();
    }
    public void reset_password(int customer_ID, String newPassword) {
        shopit = getWritableDatabase();
        ContentValues customer = new ContentValues();
        customer.put("customer_password", newPassword);
        String[] arg = {String.valueOf(customer_ID)};
        shopit.update("customer", customer, "customer_id like ?", arg);
        shopit.close();
    }


    /////////delete data from the database////////////
    private void delete_customer_security_questions(int customer_ID) {
        shopit = getWritableDatabase();
        shopit.delete("customer_question", "customer_id_fk like ?", new String[]{String.valueOf(customer_ID)});
        shopit.close();
    }
    public void remove_product_from_cart(int customer_ID, int cart_ID, int product_ID) {
        //get product taken quantity and price
        Cursor cartProductCursor = get_cart_product_data(cart_ID, product_ID);
        int ProductQuantity = cartProductCursor.getInt(2);
        float ProductPrice = cartProductCursor.getFloat(3);

        //get order old total price
        Cursor cart = get_customer_cart(customer_ID);
        float orderPrice = cart.getFloat(1);
        //float orderPrice = get_order_price(cart_ID);

        //get product old quantity
        Cursor product = get_product_data(product_ID);
        int oldQuantity = product.getInt(3);

        //remove product row from cart_product table
        shopit = getWritableDatabase();
        String[] args = {String.valueOf(cart_ID), String.valueOf(product_ID)};
        shopit.delete("cart_product ", "cart_id_fk like ? and product_id_fk like ?", args);
        //order price = old price - product price(price * quantity)
        ContentValues Row = new ContentValues();
        Row.put("order_total_price", orderPrice - ProductPrice);
        shopit.update("customer_cart", Row, "cart_id like? ", new String[]{String.valueOf(cart_ID)});

        //product quantity increase
        Row.clear();
        Row.put("product_quantity",oldQuantity+  ProductQuantity );
        shopit.update("product", Row, "product_id like? ", new String[]{String.valueOf(product_ID)});

        shopit.close();
    }
    public void submit_order(int cart_ID, int customer_ID){
        shopit = getWritableDatabase();
        shopit.delete("customer_cart", "cart_id like ?", new String[]{String.valueOf(cart_ID)});
        shopit.delete("cart_product", "cart_id_fk like ?", new String[]{String.valueOf(cart_ID)});
        add_customer_cart(customer_ID);
        shopit.close();
    }

    /////////storing data in the database////////////
    private void fill_category_table(SQLiteDatabase db) {
        int[] categoryImage = {R.drawable.womenwear, R.drawable.menwear, R.drawable.kidswear};
        String[] categoryName = {"Women wear", "Men wear", "Kids wear"};
        ContentValues categoryRow= new ContentValues();
        for(int i = 0; i< categoryImage.length; i++)
        {
            categoryRow.put("category_name", categoryName[i]);
            categoryRow.put("category_image", String.valueOf(categoryImage[i]));
            db.insert("category", null, categoryRow);
            categoryRow.clear();
        }

    }
    private void fill_product_table(SQLiteDatabase db) {
        int[] productImage = {R.drawable.pinkypants, R.drawable.darkgreenbeltedshirtdress, R.drawable.highwaistwidelegpants, R.drawable.summerblouse, R.drawable.blackandwhiteshirt, R.drawable.blueandyellowshirt, R.drawable.bluesuit, R.drawable.darkgreenpants, R.drawable.whiteshirt, R.drawable.crewneck, R.drawable.jeans, R.drawable.pants, R.drawable.shorts};
        String[] productName= {"Pinky pants", "Dark green dress", "High waist pants", "Summer blouse", "Black & white shirt", "Blue & yellow shirt", "Blue suit", "Dark green pants", "White shirt", "Kids crewneck", "Kids jeans pants", "Kids pants", "Kids shorts"};
        float[] productPrice={250, 350, 250, 150, 150, 150, 500, 250, 170, 250, 200, 150, 180};
        int[] productQuantity={9, 5, 10, 20, 25, 15, 5, 10, 25, 5, 15, 20, 10};
        int[] productCategoryID = {1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3};
        ContentValues productRow = new ContentValues();
        for(int i = 0; i < productName.length; i++)
        {
            productRow.put("product_name", productName[i]);
            productRow.put("product_image", productImage[i]);
            productRow.put("product_price", productPrice[i]);
            productRow.put("product_quantity", productQuantity[i]);
            productRow.put("product_category_id", productCategoryID[i]);
            db.insert("product", null, productRow);
            productRow.clear();
        }
    }
    private void fill_question_table(SQLiteDatabase db) {
        String[] question = {"What is your favorite olympic sport?", "What was the first thing you learned to cook?", "Who is your favorite actor/actress?", "What was your first pet's name?", "Who was your favorite school teacher?", "What was your childhood nick name?"};
        ContentValues questionRow = new ContentValues();
        for (int i = 0; i < question.length; i++) {
            questionRow.put("recovery_question_txt", question[i]);
            db.insert("recovery_question", null, questionRow);
            questionRow.clear();
        }
    }
}
