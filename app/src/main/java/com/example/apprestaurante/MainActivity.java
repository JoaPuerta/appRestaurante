package com.example.apprestaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idCustomer; //variable que contendra el IDde cada cliente (customer)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciar y referenciar los ID´s del archivo xlm
        EditText ident = findViewById(R.id.etident);
        EditText fullname = findViewById(R.id.etfullname);
        EditText email = findViewById(R.id.etemail);
        EditText password = findViewById(R.id.etpassword);
        Button btnsave = findViewById(R.id.save);
        Button btnsearch = findViewById(R.id.search);
        Button btnedit = findViewById(R.id.edit);
        Button btndelete = findViewById(R.id.delete);


        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("¿ Está seguro de eliminar el cliente con ID: "+ident.getText().toString()+"?");
                alertDialogBuilder.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                    @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                db.collection("customer").document(idCustomer)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("cliente", "DocumentSnapshot successfully deleted!");
                                                Toast.makeText(MainActivity.this,"Cliente borrado correctamente...",Toast.LENGTH_SHORT).show();
                                           ident.setText("");
                                           fullname.setText("");
                                           email.setText("");
                                           password.setText("");
                                           ident.requestFocus();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("cliente", "Error...", e);
                                            }
                                        });
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> mcliente = new HashMap<>();
                mcliente.put("ident", ident.getText().toString());
                mcliente.put("fullname", fullname.getText().toString());
                mcliente.put("email", email.getText().toString());
                mcliente.put("password", password.getText().toString());

                db.collection("customer").document(idCustomer)
                        .set(mcliente)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("cliente", "DocumentSnapshot successfully written!");
                                Toast.makeText(MainActivity.this,"Cliente actualizado correctmente...",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("cliente", "Error al escribir documento....", e);
                            }
                        });
            }
        });



        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("customer")
                        .whereEqualTo("ident", ident.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idCustomer = document.getId();
                                            fullname.setText(document.getString("fullname"));
                                            email.setText(document.getString("email"));
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"El id del cliente NO existe",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomer(ident.getText().toString(),fullname.getText().toString(), email.getText().toString(),password.getText().toString());
            }
        });
    }

    private void saveCustomer(String sident, String sfullname, String semail, String spassword) {

        //buscar la identificacion del cliente nuevo

        db.collection("customer")
                .whereEqualTo("ident", sident)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) { //NO encontro el documento, se puede guardar
                                //Guardar los datos del cliente (customer)
                                Map<String, Object> customer = new HashMap<>();
                                customer.put("ident",sident);
                                customer.put("fullname", sfullname);
                                customer.put("email", semail);
                                customer.put("password", spassword);

                                db.collection("customer")
                                        .add(customer)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Cliente agregado correctamente....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error! cliente no se agrego...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"El id del cliente extistente...",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        }
}