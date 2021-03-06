package com.example.doc_app_android.utils;

public class Globals {
    public static String serverURL = "https://maivrikdoc.herokuapp.com/api/";

    public static String loginURL = serverURL + "login";
    public static String patientRegister = serverURL + "register/patient";
    public static String docRegister = serverURL + "register/doctor";
    public static String docFilter = serverURL + "problems";
    public static String profileDoctor = serverURL + "doctor/";
    public static String profilePatient = serverURL + "patient/";
    public static String editGenDetails = serverURL + "user/";
    public static String doctorHomeScreenPatientList = serverURL + "patients?problem=";
    public static String patientList = serverURL + "patients";
    public static String checkUpHistory = serverURL + "reports/";
    public static String xray = serverURL + "xrays/";
    public static String prescription = serverURL + "prescriptions/";
    public static String appointment = serverURL + "getappointment";
    public static String docList = serverURL + "doctors";
    public static String report = serverURL + "report/";
    public static String updateUserData = serverURL + "user/";
    public static String askAppointment = serverURL + "askappointment";
    public static String addNewReport = serverURL + "report/add";
    public static String addNewXray = serverURL + "xray/add";
    public static String notifications = serverURL + "notifications";
    public static String notificationStatus = serverURL + "notification/";
    public static String newNotifications = serverURL + "getappointment";
    public static String shareReport = serverURL + "shareReport";
    public static String denyAppointment = serverURL + "denyrequest";
    public static String addUserData = serverURL + "user/";
    public static String addSpecialistList = serverURL + "registerp";
    public static String individualAppointment = serverURL + "appointments/";
    public static String cancelAppointment = serverURL + "getappointment";
    public static String addProblem = serverURL + "problem/add";
    public static String sendPrescription = serverURL + "prescription/add";
}
