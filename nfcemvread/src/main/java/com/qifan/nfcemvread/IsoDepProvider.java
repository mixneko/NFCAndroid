package com.qifan.nfcemvread;

import android.nfc.tech.IsoDep;

import com.github.devnied.emvnfccard.exception.CommunicationException;
import com.github.devnied.emvnfccard.parser.IProvider;

import java.io.IOException;

/**
 * Sends the APDU commands of the EMV parser to a contactless card.
 */
class IsoDepProvider implements IProvider {
    private final IsoDep mIsoDep;

    IsoDepProvider(IsoDep isoDep) {
        mIsoDep = isoDep;
    }

    @Override
    public byte[] transceive(byte[] command) throws CommunicationException {
        try {
            return mIsoDep.transceive(command);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }
    }

    @Override
    public byte[] getAt() {
        // NFC-A cards answer with historical bytes, NFC-B cards with a higher layer response.
        byte[] historicalBytes = mIsoDep.getHistoricalBytes();
        return historicalBytes != null ? historicalBytes : mIsoDep.getHiLayerResponse();
    }
}
