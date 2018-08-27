function Cryptokit() {
};


Cryptokit.prototype.init = function () {
    var eDiv = document.createElement("div");
    if (navigator.appName.indexOf("Internet") >= 0 || navigator.appVersion.indexOf("Trident") >= 0) {
        if (window.navigator.cpuClass == "x86") {
            eDiv.innerHTML = "<object id=\"cryptokitCtrl\" codebase=\"CryptoKit.Ultimate.x86.cab\" classid=\"clsid:4C588282-7792-4E16-93CB-9744402E4E98\" ></object>";
        }
        else {
            eDiv.innerHTML = "<object id=\"cryptokitCtrl\" codebase=\"CryptoKit.Ultimate.x64.cab\" classid=\"clsid:B2F2D4D4-D808-43B3-B355-B671C0DE15D4\" ></object>";
        }
    }
    else {
        eDiv.innerHTML = "<embed id=\"cryptokitCtrl\" type=\"application/npCryptoKit.Ultimate.x86\" style=\"height: 0px; width: 0px\">";
    }
    document.body.appendChild(eDiv);
}


Cryptokit.prototype.uninit = function () {
}

Cryptokit.prototype.setSM2CSPList = function (bstrCSPList, callback) {
    var result = new Object() ;
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").SetSM2CSPList(bstrCSPList);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}

Cryptokit.prototype.selectCertificate = function (bstrSubjectDNFilter, bstrIssuerDNFilter, serialNo, callback) {
    var result = new Object() ;
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").SelectCertificate(bstrSubjectDNFilter, bstrIssuerDNFilter, serialNo);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.getSignCertInfo = function (InfoTypeID, callback) {
    var result = new Object(); 
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").GetSignCertInfo(InfoTypeID);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.signMsgPKCS7 = function (source, selectedAlg, IsWithSource, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").SignMsgPKCS7(source, selectedAlg, IsWithSource); 
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.verifyMsgSignaturePKCS7Attached = function (signature, signaturetype, callback)  {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").VerifyMsgSignaturePKCS7Attached(signature, signaturetype); 
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.verifyMsgSignaturePKCS7Detached = function (signature, signaturetype, source, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").VerifyMsgSignaturePKCS7Detached(signature, signaturetype, source); 
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.selectEncryptCertificate = function (subjectDNFilter, issuerDNFilter, serialNumFilter, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").SelectEncryptCertificate(subjectDNFilter, issuerDNFilter, serialNumFilter);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.getEncryptCertInfo = function (InfoTypeID, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").GetEncryptCertInfo(InfoTypeID);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.encryptMsgCMSEnvelope = function (encryptmassage, encryptalg, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").EncryptMsgCMSEnvelope(encryptmassage, encryptalg);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.encryptMsgCMSEnvelope_ByCert = function (base64certdata, certtype, encryptmassage, encryptalg, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").EncryptMsgCMSEnvelope_ByCert(base64certdata, certtype, encryptmassage, encryptalg);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.encryptMsgCMSEnvelopeEx = function (encryptmassage, encryptalg, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").EncryptMsgCMSEnvelopeEx(encryptmassage, encryptalg);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.encryptMsgCMSEnvelopeEx_ByCert = function (base64certdata, certtype, encryptmassage, encryptalg, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").EncryptMsgCMSEnvelopeEx_ByCert(base64certdata, certtype, encryptmassage, encryptalg);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.decryptMsgCMSEnvelope = function (envelope, decrypttype, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").DecryptMsgCMSEnvelope(envelope, decrypttype);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.decryptMsgCMSEnvelope_BySoftCert = function (filename, filepassword, envelope, decrypttype, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").DecryptMsgCMSEnvelope_BySoftCert(filename, filepassword, envelope, decrypttype);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.decryptMsgCMSEnvelopeEx = function (envelope, decrypttype, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").DecryptMsgCMSEnvelopeEx(envelope, decrypttype);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.decryptMsgCMSEnvelopeEx_BySoftCert = function (filename, filepassword, envelope, decrypttype, callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").DecryptMsgCMSEnvelopeEx_BySoftCert(filename, filepassword, envelope, decrypttype);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
    callback(result);
}


Cryptokit.prototype.GetLastErrorDesc = function (callback) {
    var result = new Object();
    try {
        result.error = 0;
        result.value = document.getElementById("cryptokitCtrl").GetLastErrorDesc();
        callback(result);
    }
    catch (e) {
        result.error = -1;
        result.value = e.message;
    }
}
