package cs562OSU.imageproject;

        import android.app.AlertDialog;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity {

    ImageView firstImg,secondImg;
    Button btnCamera,btnLibrary;
    CheckBox fir, iir;
    EditText Inter,Decimation;
    TextView FirstSize, SecondSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstImg=(ImageView)findViewById(R.id.imgFirst);
        secondImg=(ImageView)findViewById(R.id.imgSecond);
        btnCamera=(Button)findViewById(R.id.btnCamera);
        btnLibrary=(Button)findViewById(R.id.btnLibrary);
        fir =(CheckBox)findViewById(R.id.chkF);
        iir =(CheckBox)findViewById(R.id.chkIir);
        Inter = (EditText)findViewById(R.id.etInter);
        Decimation = (EditText)findViewById(R.id.etDec);
        FirstSize = (TextView)findViewById(R.id.fisrtImageSize);
        SecondSize = (TextView)findViewById(R.id.secondImageSize);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        btnLibrary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Uri targetUri = data.getData();
        // decimation and interpolation numbers are taken first
        int interpolator = Integer.parseInt(Inter.getText().toString());
        int decimator = Integer.parseInt(Decimation.getText().toString());
       //Photos as bitmaps
        Bitmap firts = null;
        Bitmap second = null;

        boolean filter = true;

        try {
            if (requestCode == 0)
            {
                firts = (Bitmap) data.getExtras().get("data");
            }
            else if (requestCode == 1)
            {
                firts = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            }
            else
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle("Fail");
                        builder1.setMessage("Failed to load");
                        builder1.setCancelable(true);
            }

            firstImg.setImageBitmap(firts);


            second = Bitmap.createScaledBitmap(firts, ((interpolator * firts.getWidth()) / decimator),
                    ((interpolator * firts.getHeight()) / decimator), filter);
            secondImg.setImageBitmap(second);


            FirstSize.setText(Integer.toString(firts.getWidth()) + "x" + Integer.toString(firts.getHeight()));
            SecondSize.setText(Integer.toString(second.getWidth()) + "x" + Integer.toString(second.getHeight()));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}