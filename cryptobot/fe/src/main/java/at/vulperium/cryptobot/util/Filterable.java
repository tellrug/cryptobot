package at.vulperium.cryptobot.util;

import at.vulperium.cryptobot.tradejobs.vo.FilterVO;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public interface Filterable {

    boolean filtering(FilterVO filterVO);
}
