package brainwind.letstalksample.forms.general;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hbb20.CountryCodePicker;

import brainwind.letstalksample.R;
import ernestoyaquello.com.verticalstepperform.Step;


public class SelectCountry extends Step<String>
{

    CountryCodePicker ccp;
    Context context;

    public SelectCountry(Context context,String title, String subtitle) {
        super(title, subtitle);
        this.context=context;
    }

    @Override
    public String getStepData() {
        return ccp != null ? ccp.getSelectedCountryCode().trim().toLowerCase() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return ccp != null ? ccp.getSelectedCountryName() : "Please select your country";
    }

    @Override
    public void restoreStepData(String data) {
        if(ccp!=null)
        {

        }
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        String setpdata=getStepData();
        String errorMessage = setpdata.trim().isEmpty() ? "Please select your country" : "";
        return new IsDataValid(setpdata.trim().isEmpty()==false, errorMessage);
    }

    @Override
    protected View createStepContentLayout() {

        View view= LayoutInflater.from(context).inflate(R.layout.form_select_country,
                null,false);
        ccp=view.findViewById(R.id.ccp);
        return view;

    }

    @Override
    protected void onStepOpened(boolean animated) {

        ccp.requestFocus();

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }


    public void setCountryCode(int country_code)
    {

        if(ccp!=null)
        {
            ccp.setCountryForPhoneCode(country_code);

        }

    }

    public void setCountry(String country)
    {
        if(ccp!=null)
        {

            ccp.setCountryForNameCode(country);

        }

    }

}
