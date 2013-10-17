
function disableGUIButtons() {

	var myUploadField1 = document.getElementById('imgupload-use-existing');
	if (myUploadField1 != null) {
	    myUploadField1.disabled = true;
	}
	myUploadField1 = document.getElementById('imgupload-create-new');
	if (myUploadField1 != null) {
	    myUploadField1.disabled = true;
	}
    
}

function enableGUIButtons() {

	var myUploadField1 = document.getElementById('imgupload-use-existing');
	if (myUploadField1 != null) {
	    myUploadField1.disabled = false;
	}
	myUploadField1 = document.getElementById('imgupload-create-new');
	if (myUploadField1 != null) {
	    myUploadField1.disabled = false;
	}
    
}