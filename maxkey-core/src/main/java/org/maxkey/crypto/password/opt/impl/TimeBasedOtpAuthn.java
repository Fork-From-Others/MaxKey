package org.maxkey.crypto.password.opt.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Hex;
import org.maxkey.crypto.Base32Utils;
import org.maxkey.crypto.password.opt.AbstractOptAuthn;
import org.maxkey.crypto.password.opt.algorithm.TimeBasedOTP;
import org.maxkey.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class TimeBasedOtpAuthn extends AbstractOptAuthn {
    private static final  Logger _logger = LoggerFactory.getLogger(TimeBasedOtpAuthn.class);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TimeBasedOtpAuthn(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public boolean produce(UserInfo userInfo) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean validate(UserInfo userInfo, String token) {
        _logger.debug("utcTime : " + dateFormat.format(new Date()));
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        byte[] byteSharedSecret = Base32Utils.decode(userInfo.getSharedSecret());
        String hexSharedSecret = Hex.encodeHexString(byteSharedSecret);
        String timeBasedToken = "";
        if (crypto.equalsIgnoreCase("HmacSHA1")) {
            timeBasedToken = TimeBasedOTP.genOTP(
                    hexSharedSecret,
                    Long.toHexString(currentTimeSeconds / interval).toUpperCase() + "",
                    digits + "");
        } else if (crypto.equalsIgnoreCase("HmacSHA256")) {
            timeBasedToken = TimeBasedOTP.genOTPHmacSHA256(
                    hexSharedSecret,
                    Long.toHexString(currentTimeSeconds / interval).toUpperCase() + "", 
                    digits + "");
        } else if (crypto.equalsIgnoreCase("HmacSHA512")) {
            timeBasedToken = TimeBasedOTP.genOTPHmacSHA512(
                    hexSharedSecret,
                    Long.toHexString(currentTimeSeconds / interval).toUpperCase() + "", 
                    digits + "");
        }
        _logger.debug("token : " + token);
        _logger.debug("timeBasedToken : " + timeBasedToken);
        if (token.equalsIgnoreCase(timeBasedToken)) {
            return true;
        }
        return false;

    }

}
