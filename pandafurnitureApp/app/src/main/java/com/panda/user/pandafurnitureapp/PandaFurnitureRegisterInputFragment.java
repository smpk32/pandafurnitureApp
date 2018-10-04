package com.panda.user.pandafurnitureapp;

        import android.content.Context;
        import android.location.Address;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.ContextMenu;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.panda.user.pandafurnitureapp.item.FoodInfoItem;
        import com.panda.user.pandafurnitureapp.lib.EtcLib;
        import com.panda.user.pandafurnitureapp.lib.GoLib;
        import com.panda.user.pandafurnitureapp.lib.MyLog;
        import com.panda.user.pandafurnitureapp.lib.MyToast;
        import com.panda.user.pandafurnitureapp.lib.StringLib;
        import com.panda.user.pandafurnitureapp.remote.RemoteService;
        import com.panda.user.pandafurnitureapp.remote.ServiceGenerator;

        import org.parceler.Parcels;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class PandaFurnitureRegisterInputFragment extends Fragment implements View.OnClickListener {

    public static final String INFO_ITEM = "INFO_ITEM";
    private final String TAG = this.getClass().getSimpleName();

    Context context;
    FoodInfoItem infoItem;
    // Address address;

    EditText nameEdit;
    EditText kindsEdit;
    EditText whenEdit;
    EditText statusEdit;
    EditText telEdit;
    EditText descriptionEdit;
    TextView currentLength;

    public static PandaFurnitureRegisterInputFragment newInstance(FoodInfoItem infoItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INFO_ITEM, Parcels.wrap(infoItem));

        PandaFurnitureRegisterInputFragment fragment = new PandaFurnitureRegisterInputFragment();
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            infoItem = Parcels.unwrap(getArguments().getParcelable(INFO_ITEM));
            if (infoItem.seq != 0) {
                PandaFurnitureRegisterActivity.currentItem = infoItem;
            }
            MyLog.d(TAG, "infoItem " + infoItem);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        // address = GeoLib.getInstance().getAddressString(context,
        //        new LatLng(infoItem.latitude, infoItem.longitude));
        // MyLog.d(TAG, "address" + address);

        return inflater.inflate(R.layout.fragment_pandafurniture_register_input, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentLength = (TextView) view.findViewById(R.id.current_length);
        nameEdit = (EditText) view.findViewById(R.id.pandafurniture_name);
        kindsEdit = (EditText) view.findViewById(R.id.pandafurniture_kinds);
        telEdit = (EditText) view.findViewById(R.id.pandafurniture_tel);
        whenEdit = (EditText) view.findViewById(R.id.pandafurniture_when);
        statusEdit = (EditText) view.findViewById(R.id.pandafurniture_status);
        descriptionEdit = (EditText) view.findViewById(R.id.pandafurniture_description);
        descriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentLength.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        EditText addressEdit = (EditText) view.findViewById(R.id.bestfood_address);

//        infoItem.address = GeoLib.getInstance().getAddressString(address);
//        if (!StringLib.getInstance().isBlank(infoItem.address)) {
//            addressEdit.setText(infoItem.address);
//        }

        Button prevButton = (Button) view.findViewById(R.id.prev);
        prevButton.setOnClickListener(this);

        Button nextButton = (Button) view.findViewById(R.id.next);
        nextButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        infoItem.name = nameEdit.getText().toString();
        infoItem.tel = telEdit.getText().toString();
        infoItem.description = descriptionEdit.getText().toString();
        MyLog.d(TAG, "onClick imageItem " + infoItem);

        if (v.getId() == R.id.next) {
            save();
        }
    }


    private void save() {
        if (StringLib.getInstance().isBlank(infoItem.name)) {
            MyToast.s(context, context.getResources().getString(R.string.input_pandafurniture_name));
            return;
        }

        if (StringLib.getInstance().isBlank(infoItem.tel)
                || !EtcLib.getInstance().isValidPhoneNumber(infoItem.tel)) {
            MyToast.s(context, context.getResources().getString(R.string.not_valid_tel_number));
            return;
        }

        insertFurnitureInfo();
    }



    private void insertFurnitureInfo() {
        MyLog.d(TAG, infoItem.toString());

        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.insertFoodInfo(infoItem);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    int seq = 0;
                    String seqString = response.body();

                    try {
                        seq = Integer.parseInt(seqString);
                    } catch (Exception e) {
                        seq = 0;
                    }

                    if (seq == 0) {
                        //등록 실패
                    } else {
                        infoItem.seq = seq;
                        goNextPage();
                    }
                } else { // 등록 실패
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    MyLog.d(TAG, "fail " + statusCode + errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
            }
        });
    }



    private void goNextPage() {
//        GoLib.getInstance().goFragmentBack(getFragmentManager(),
//                R.id.content_main, PandaFurnitureRegisterImageFragment.newInstance(infoItem.seq));
    }




}
