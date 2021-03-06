package me.rainstorm.innodb.common.i18n;

/**
 * @author traceless
 */

public enum I18nMsgCodeEnum {
    //----------Command Line Option Description-----------------
    OptionUsageSyntax,
    OptionUsageHeader,
    OptionUsageFooter,
    OptionDataDir,
    OptionSystemTableSpace,
    OptionVerbose,
    OptionHelp,
    OptionVersion,
    OptionI18N,
    OptionDatabase,
    OptionTable,
    OptionPage,
    OptionExport,

    //-----------------------LOG--------------------------------
    LogSystemTableSpacePath,
    LogIndependentTableSpacePath,
    LogPageTypeDetailNotSupport,
    LogCommandLineExecuteStrategyMatchOrder,
    LogCommandLineExecuteStrategyMatched,
    LogCommandLineExecuteStrategyDisMatched,
    LogPageLocate,
    LogPageNumInExtentLessThanExpected,
    LogLoadExtentFailure,
    LogTableSpaceSummary,
    LogLocaleNotSupport,
    //--------------------Term begin----------------------------

    // PageType
    TermPageTypeAllocated,
    TermPageTypeUndoLog,
    TermPageTypeInode,
    TermPageTypeInsertBufferFreeList,
    TermPageTypeIBufBitmap,
    TermPageTypeSystem,
    TermPageTypeTransactionSystem,
    TermPageTypeFileSpaceHeader,
    TermPageTypeExtentDescriptor,
    TermPageTypeBlob,
    TermPageTypeZBlob,
    TermPageTypeZBlob2,
    TermPageTypeUnknown,
    TermPageTypeCompressed,
    TermPageTypeEncrypted,
    TermPageTypeCompressedAndEncrypted,
    TermPageTypeEncryptedRTree,
    TermPageTypeIndex,
    TermPageTypeRTree,
    //--------------------Term end------------------------------

    //---------------Common Constants---------------------------
    ConstDefault,

    //-----------------------Other------------------------------
    Greeting;
}
