package ir.rahkarpouya.rpandroidlib.crashlytics

data class SetCrashReport(
    var BuildVersion: String,
    var Device: String,
    var StackTrace: String,
    var UserAction: String,
    var UserID: Int
) {

    constructor() : this("", "", "", "", -1)

    constructor(setCrashReport: SetCrashReport) : this(
        setCrashReport.BuildVersion,
        setCrashReport.Device,
        setCrashReport.StackTrace,
        setCrashReport.UserAction,
        setCrashReport.UserID
    )
}