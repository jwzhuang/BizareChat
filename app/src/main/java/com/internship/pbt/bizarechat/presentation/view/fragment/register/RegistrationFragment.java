package com.internship.pbt.bizarechat.presentation.view.fragment.register;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.internship.pbt.bizarechat.R;
import com.internship.pbt.bizarechat.presentation.model.FacebookLinkInform;
import com.internship.pbt.bizarechat.presentation.model.InformationOnCheck;
import com.internship.pbt.bizarechat.presentation.presenter.registration.RegistrationPresenter;
import com.internship.pbt.bizarechat.presentation.presenter.registration.RegistrationPresenterImpl;
import com.internship.pbt.bizarechat.presentation.view.fragment.BaseFragment;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.tinkoff.decoro.watchers.FormatWatcher;

import static android.app.Activity.RESULT_OK;

public class RegistrationFragment extends BaseFragment implements RegistrationView, View.OnClickListener {

    private static final String PACKAGE_PATH = "com.internship.pbt.bizarechat.presentation.view.fragment.register";
    private final int DEVICE_CAMERA = 0;
    private final int PHOTO_GALLERY = 1;

    private CallbackManager callbackManager;

    private RegistrationPresenter mRegistrationPresenter;

    private TextInputLayout mEmailLayout,
            mPasswordLayout,
            mPasswordConfLayout,
            mPhoneLayout;
    private FrameLayout mImageWrapper;

    private EditText mEmailEditText;

    private TextInputEditText mPasswordEditText,
            mPasswordConfirm,
            mPhoneEditText;

    private Button mSignUpButton;
    private Button mFacebookLinkButton;
    private OnRegisterSuccess mOnRegisterSuccess;


    private CircleImageView mAvatarImage;
    private Animation mSuccessFacebookButtonAnim;
    private Animation mFailButtonAnim;
    private Animation getmSuccessSignUpAnim;

    private LoginButton mFacebookButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRegisterSuccess) {
            mOnRegisterSuccess = (OnRegisterSuccess) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("123", "Fragment OnCreate");
        mRegistrationPresenter = new RegistrationPresenterImpl();
        mRegistrationPresenter.setRegistrationView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("123", "Fragment OnCreateView");

        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAvatarImage = (CircleImageView) v.findViewById(R.id.user_pic);
        mImageWrapper = (FrameLayout)v.findViewById(R.id.image_wrapper);


        mEmailLayout = (TextInputLayout) v.findViewById(R.id.text_input_email);
        mPasswordLayout = (TextInputLayout) v.findViewById(R.id.text_input_password);
        mPasswordConfLayout = (TextInputLayout) v.findViewById(R.id.text_input_password_confirm);
        mPhoneLayout = (TextInputLayout) v.findViewById(R.id.text_input_phone);

        mEmailEditText = (EditText) v.findViewById(R.id.register_email);
        mPasswordEditText = (TextInputEditText) v.findViewById(R.id.register_password);
        mPasswordConfirm = (TextInputEditText) v.findViewById(R.id.register_confirm_password);
        mPhoneEditText = (TextInputEditText) v.findViewById(R.id.register_phone);

        mFacebookLinkButton = (Button) v.findViewById(R.id.login_facebook_button);
        mSignUpButton = (Button) v.findViewById(R.id.register_sign_up);
        mRegistrationPresenter.createFormatWatcher();

        LoginManager.getInstance().logOut();
        this.setCallbackToLoginFacebookButton();
        mFacebookLinkButton.setOnClickListener(l -> LoginManager.getInstance().logInWithReadPermissions(RegistrationFragment.this, Arrays.asList("public_profile")));
        mImageWrapper.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        this.setAnimation();

        return v;
    }

    private void setCallbackToLoginFacebookButton (){
        Log.d("123", "OnSuccess " + "setCallBack");

        callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Bundle param = new Bundle();
                    param.putString("fields", "id, email");
                    Log.d("123", "OnSuccess FRAGMENT INF" + loginResult.getAccessToken().getUserId());
                    mRegistrationPresenter.facebookLink(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.d("123", "OnCancel");

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("123", error.toString());
                }
            });

    }

   /* private void addPhoneNumberFormatting() {
        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots(getActivity().getResources().
                getString(R.string.phone_format));
        FormatWatcher formatWatcher = new MaskFormatWatcher(
                MaskImpl.createTerminated(slots)
        );
        formatWatcher.installOn(mPhoneEditText);
        mPhoneEditText.setSelection(mPhoneEditText.getText().length());
    }*/

    @Override
    public void addPhoneNumberFormatting(FormatWatcher formatWatcher) {
        formatWatcher.installOn(mPhoneEditText);
        mPhoneEditText.setSelection(mPhoneEditText.getText().length());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView txtView = (TextView) getActivity().findViewById(R.id.toolbar_title);
        txtView.setText(R.string.sign_up);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRegistrationPresenter.destroy();
    }

    @Override
    public void setAnimation() {

    }

    @Override
    public void startFailedSignUpAnim() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void startOnFacebookLinkSuccessAnim() {
        mFacebookLinkButton.setEnabled(false);
        mFacebookLinkButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.disappear));
        mFacebookLinkButton.setText(getText(R.string.linked));
        mFacebookLinkButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.appear));
    }

    @Override
    public void startOnFailedFacebooLinkkAnim() {
    }

    @Override
    public void startSuccessSignUpAnim() {

    }

    @Override
    public void showLoading() {
    }


    @Override
    public void hideLoading() {
    }

    @Override
    public void showRetry() {
    }

    @Override
    public void showError(String message) {
    }

    @Override

    public void hideErrorInvalidEmail() {
        mEmailLayout.setError(null);
        mEmailLayout.setErrorEnabled(false);
    }

    @Override
    public void hideErrorInvalidPassword() {
        mPasswordLayout.setError(null);
        mPasswordLayout.setErrorEnabled(false);
    }


    public void hideErrorPasswordConfirm() {
        mPasswordConfLayout.setError(null);
        mPasswordConfLayout.setErrorEnabled(false);
    }

    @Override
    public void hideErrorInvalidPhone() {
        mPhoneLayout.setError(null);
        mPhoneLayout.setErrorEnabled(false);
    }

    @Override
    public void showErrorInvalidEmail() {
        mEmailLayout.setErrorEnabled(true);
        mEmailLayout.setError(getString(R.string.invalid_email));
        mPasswordEditText.setText("");

    }

    @Override
    public void showErrorInvalidPassword() {
        mPasswordLayout.setErrorEnabled(true);
        mPasswordLayout.setError(getString(R.string.invalid_weak_password));
        mPasswordEditText.setText("");
    }

    @Override
    public void showErrorInvalidPhone() {
        mPhoneLayout.setErrorEnabled(true);
        mPhoneLayout.setError(getString(R.string.invalid_phone));
        mPhoneEditText.setText("");
    }

    @Override
    public void showErrorPasswordLength() {
        mPasswordLayout.setError(getString(R.string.error_password_length));
        mPasswordEditText.setText("");
    }

    @Override
    public void refreshInfAfterFacebookLink(FacebookLinkInform linkInform) {
        Log.d("123", "RegistrationFragment" + linkInform.toString());

        mEmailEditText.setText(linkInform.getEmail());
        mPhoneEditText.setText(linkInform.getPhoneNum());
    }

    @Override
    public void onRegistrationSuccess() {
        mOnRegisterSuccess.onRegisterSuccess();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_sign_up:
                this.getInformationForValidation();
                break;
            case R.id.login_facebook_button:
                mRegistrationPresenter.facebookLink();
                break;
            case R.id.image_wrapper:
                this.showPictureChooser();
                break;
        }
    }


    @Override
    public void showPictureChooser() {
        final CharSequence[] items = {getText(R.string.device_camera),
                getText(R.string.photo_gallery)};
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getText(R.string.choose_source_for_getting_image));
        builder.setNegativeButton(R.string.back, null);
        builder.setItems(items, (dialogInterface, i) -> {
            if (items[i].equals(getText(R.string.device_camera))) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, DEVICE_CAMERA);
            } else if (items[i].equals(getText(R.string.photo_gallery))) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PHOTO_GALLERY);
            }
        });
        builder.show();
    }

    @Override
    public void getInformationForValidation() {
        InformationOnCheck informationOnCheck = new InformationOnCheck();
        informationOnCheck.setEmail(mEmailEditText.getText().toString());
        informationOnCheck.setPassword(mPasswordEditText.getText().toString());
        informationOnCheck.setPhone(mPhoneEditText.getText().toString());
        informationOnCheck.setPasswordConf(mPasswordConfirm.getText().toString());
        Log.d("123", "Fragment GetValidInf" + informationOnCheck.toString());
        mRegistrationPresenter.validateInformation(informationOnCheck);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK && requestCode == DEVICE_CAMERA) {
            mRegistrationPresenter.verifyAndLoadAvatar(data.getData());
        }

        if (data != null && resultCode == RESULT_OK && requestCode == PHOTO_GALLERY) {
            mRegistrationPresenter.verifyAndLoadAvatar(data.getData());
        }

        if(resultCode == RESULT_OK)
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void loadAvatarToImageView(Uri uri) {
        Glide.with(this).load(uri).centerCrop().into(mAvatarImage);
    }

    @Override
    public void makeAvatarSizeToast() {
        Toast.makeText(this.getContextActivity(), getText(R.string.too_large_picture_max_size_1mb), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContextActivity() {
        return getActivity();
    }

    public void showErrorPasswordConfirm() {
        mPasswordConfirm.setText("");
        Toast.makeText(this.getContextActivity(), R.string.do_not_match_password, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Context getContextActivity() {
        return getActivity();
    }

    public interface OnRegisterSuccess {
        void onRegisterSuccess();
    }
}
