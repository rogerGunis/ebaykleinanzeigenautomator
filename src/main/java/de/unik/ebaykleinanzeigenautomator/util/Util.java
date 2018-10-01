package de.unik.ebaykleinanzeigenautomator.util;

import com.codeborne.selenide.Selenide;

public class Util
{
    public static void waitOnPageLoad(int waitTime)
    {
        int waitTimeRangeHalf = (int)Math.round(waitTime * Context.get().getConfiguration().projectDelayOffsetPercentage());
        Selenide.sleep(waitTime - waitTimeRangeHalf + new java.util.Random().nextInt(2 * waitTimeRangeHalf));
    }
}
