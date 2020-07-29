package ir.rahkarpouya.rpandroidlib.crashlytics

data class GetCrashReport(var ResultCode: Int, var ResultDescription: String) {
    constructor() : this(-1, "خظا در شبکه لطفا در فرصت دیگر امتحان کتید")
}