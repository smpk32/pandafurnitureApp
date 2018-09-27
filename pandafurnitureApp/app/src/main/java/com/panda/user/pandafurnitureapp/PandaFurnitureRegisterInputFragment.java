package com.panda.user.pandafurnitureapp;

        import android.content.Context;
        import android.location.Address;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.ContextMenu;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.panda.user.pandafurnitureapp.item.FoodInfoItem;

public class PandaFurnitureRegisterInputFragment extends Fragment implements View.OnClickListener {

    public static final string INFO_ITEM = "INFO_ITEM";
    private final String TAG = this.getClass().getSimpleName();

    Context context;
    FoodInfoItem infoItem;
    // Address address;

    EditText nameEdit;
    EditText kindsEdit;
    EditText whenEdit;
    EditText statusEdit;
    EditText telEdit;
    EditText descriptionEdt;
    TextView currentLength;



    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        // address = GeoLib.getInstance().getAddressString(context,
        //        new LatLng(infoItem.latitude, infoItem.longitude));
        // MyLog.d(TAG, "address" + address);

        return inflater.inflate(R.layout.fragment_pandafurniture_register_input, container, false);
    }



}
