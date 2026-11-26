package com.qifan.nfcemvread;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;

import com.github.devnied.emvnfccard.enums.EmvCardScheme;
import com.github.devnied.emvnfccard.model.EmvCard;
import com.github.devnied.emvnfccard.model.enums.CardStateEnum;
import com.github.devnied.emvnfccard.parser.EmvTemplate;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Qifan on 27/11/2018.
 */
public class MyCardNfcAsyncTask extends AsyncTask<Void, Void, Object> {
    public static class Builder {
        private Tag mTag;
        private MyCardNfcInterface mInterface;
        private boolean mFromStart;


        public Builder(MyCardNfcInterface i, Intent intent, boolean fromCreate) {
            mInterface = i;
            mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mFromStart = fromCreate;
        }

        public MyCardNfcAsyncTask build() {
            return new MyCardNfcAsyncTask(this);
        }
    }


    public interface MyCardNfcInterface {
        void startNfcReadCard();

        void cardIsReadyToRead();

        void doNotMoveCardSoFast();

        void unknownEmvCard();

        void cardWithLockedNfc();

        void finishNfcReadCard();
    }

    public final static String CARD_UNKNOWN = "UNKNOWN";
    public final static String CARD_VISA = EmvCardScheme.VISA.name();
    public final static String CARD_MASTER_CARD = EmvCardScheme.MASTER_CARD.name();

    private final static String NFC_A_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]";
    private final static String NFC_B_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcB]";
    private boolean mException;
    private EmvCard mCard;
    private MyCardNfcInterface mInterface;
    private Tag mTag;
    private String mCardNumber;
    private String mExpireDate;
    private String mCardType;
    private String mCardHolderFirstName;
    private String mCardHolderLastName;

    private MyCardNfcAsyncTask(Builder b) {
        mTag = b.mTag;
        if (mTag != null) {
            mInterface = b.mInterface;
            try {
                if (mTag.toString().equals(NFC_A_TAG) || mTag.toString().equals(NFC_B_TAG)) {
                    execute();
                } else {
                    if (!b.mFromStart) {
                        mInterface.unknownEmvCard();
                    }
                    clearAll();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardExpireDate() {
        return mExpireDate;
    }

    public String getCardType() {
        return mCardType;
    }

    public String getCardHolderFirstName() {
        return mCardHolderFirstName;
    }

    public String getCardHolderLastName() {
        return mCardHolderLastName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mInterface.startNfcReadCard();
    }

    @Override
    protected Object doInBackground(Void... voids) {
        Object result = null;

        try {
            doInBackground();
        } catch (Exception e) {
            result = e;
            Log.e(MyCardNfcAsyncTask.class.getName(), e.getMessage(), e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(final Object result) {
        if (!mException) {
            if (mCard != null) {
                if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                    mCardNumber = mCard.getCardNumber();
                    mExpireDate = formatExpireDate(mCard.getExpireDate());
                    mCardType = mCard.getType() == null ? CARD_UNKNOWN : mCard.getType().name();
                    mCardHolderFirstName = mCard.getHolderFirstname();
                    mCardHolderLastName = mCard.getHolderLastname();
                    mInterface.cardIsReadyToRead();
                } else if (mCard.getState() == CardStateEnum.LOCKED) {
                    mInterface.cardWithLockedNfc();
                } else {
                    mInterface.unknownEmvCard();
                }
            } else {
                mInterface.unknownEmvCard();
            }
        } else {
            mInterface.doNotMoveCardSoFast();
        }
        mInterface.finishNfcReadCard();
        clearAll();
    }

    private void doInBackground() {
        IsoDep mIsoDep = IsoDep.get(mTag);
        if (mIsoDep == null) {
            mException = true;
            return;
        }
        mException = false;

        // Closing the IsoDep ends the connection to the card.
        try (IsoDep isoDep = mIsoDep) {
            // Open connection
            isoDep.connect();

            EmvTemplate parser = EmvTemplate.Builder()
                    .setProvider(new IsoDepProvider(isoDep))
                    .setConfig(EmvTemplate.Config()
                            .setContactLess(true)
                            .setReadTransactions(false)
                            .setReadAt(false))
                    .build();
            mCard = parser.readEmvCard();
        } catch (IOException e) {
            mException = true;
        }
    }

    private static String formatExpireDate(Date date) {
        return date == null ? null : new SimpleDateFormat("MM/yy", Locale.US).format(date);
    }

    private void clearAll() {
        mInterface = null;
        mCard = null;
        mTag = null;
        mCardNumber = null;
        mExpireDate = null;
        mCardType = null;
    }
}
