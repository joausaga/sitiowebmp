/*
	$Id$
	prog_gallery module's javascript file
*/

/* 
	body onload
*/
$(document).ready(
	gallery_list_pageload
);

// function is executed when page is fully loaded
function gallery_list_pageload() {
	
	// load gallery contents
	gallery_list_load();
		
	$('body').append($('.jqmWindow'));
	$('.gallery_editnodes_dialog').jqm({ modal : true }); 
	$('.gallery_addimages_dialog').jqm({ modal : true });
	
	// start queue checker for no-swfu uploader
	setInterval(gallery_addimages_noswfu_queuecheck,1000) ;
}

/* 
	load image list
*/
function gallery_list_load(){
	$('.gallery_list').html('<img src="' + prog_gallery_base + 'images/loading.gif" />');
	
	// loads images and parameters as gallery_list array
	$.getScript(base_path + prog_gallery_q + 'prog_gallery_ajax/get_album_contents', function(){
		$('.gallery_list').html('');
		var ind = -1;
		if (typeof(gallery_content) !== "array") {
  		for (key in gallery_content) {
  			if (key != 'indexOf') {
  				ind++;
  				gallery_list_addentry(ind);
  			}
  		}
  	}
		
		gallery_list_makesortable();
		
		gallery_list_disablemoving = false;
		
		$("#gallery_editbuttons_update").attr("disabled","disabled");
		$("#gallery_editbuttons_cancel").attr("disabled","disabled");
	});
	
}

/* 
	insert image entry to gallery_list
*/
function gallery_list_addentry(ind) {
		var content = '<li class="gallery_entry" id="gallery_entry-' + decodeURIComponent(gallery_content[ind]['nid']) + '" onClick="gallery_selectfield(this)" onMouseOver="gallery_draggablefield(this,true)" onMouseOut="gallery_draggablefield(this,false)">';
		content += '<img class="gallery_entry_pic" src="' + base_path + decodeURIComponent(gallery_content[ind]['fails']) + '" alt="" />';
		content += '<span class="gallery_favpic">';
		if (decodeURIComponent(galleries['thumb']) == decodeURIComponent(gallery_content[ind]['nid'])) content += '<img src="' + prog_gallery_base + 'images/button_icons/favorite.gif" align="top">';
		content += '</span>';
		content += '<br />';
		content += '<div class="gallery_entry_handler">' + decodeURIComponent(gallery_content[ind]['title']) + '</div>';
		content += '</li>'
		
		$('.gallery_list').append(content);
}

/*
	makes gallery_list fields sortable
*/
function gallery_list_makesortable() {
	$(".gallery_list").sortable({
		items: 			'.gallery_entry' ,
		tolerance:		'pointer',
		placeholder :		'sortablehover',
		opacity:		0.5,
		handle:			'.gallery_entry_pic',
		tolerance:		'pointer',
		revert: 		true,
		update :		function(ser) 
		{
			gallery_warnchanges(true);
		}
	});
}

/* 
	removes sortable feature from fields
*/
function gallery_list_destroysortable() {
	$('div.gallery_list').sortable( "destroy" );
}

/*
	toggle class of handler. hover class is handler for sortable field. toggleClass function caused some glitches.
*/
function gallery_draggablefield(elem,show){
	if (gallery_list_disablemoving == false) {
		//if (show == true) $(elem).children(".gallery_entry_handler").addClass("gallery_entry_handler_hover");
		//else $(elem).children(".gallery_entry_handler").removeClass("gallery_entry_handler_hover");
	}
}

/* 
	toggles class for selected field. 
*/
function gallery_selectfield(elem){
	if (gallery_list_disablemoving == false) {
		$(elem).toggleClass("gallery_selected");
		if ($('.gallery_selected').size() > 0) {
			$("#gallery_editbuttons_edit").removeAttr("disabled");
			$("#gallery_editbuttons_remove").removeAttr("disabled");
			$("#gallery_editbuttons_thumb").removeAttr("disabled");
		}
		else {
			$("#gallery_editbuttons_edit").attr("disabled","disabled");
			$("#gallery_editbuttons_remove").attr("disabled","disabled");
			$("#gallery_editbuttons_thumb").attr("disabled","disabled");
		}
	}
}

/*
	shows a dialog with selected fields (nodes)
*/
function gallery_edit_edit(){
	if ($('.gallery_selected').size() > 0) {
		gallery_editnodes_dialog();
		$('.gallery_editnodes_dialog').jqmShow(); 
	}
}

/*
	actual dialog with selected nodes
*/
function gallery_editnodes_dialog() {
	var content = '<h2>' + gallery_t['images_information'] + '</h2>';
	content += '<hr />';
	
	content += '<div class="gallery_editnodes_dialog_entries">';
	$('.gallery_selected').each( function (i) {
			content += '<div style="margin-top:25px;">';
			
			var nid = this.id.split("-");
			nid = nid[1];
			var id = gallery_id_by_nid(gallery_content, nid);
			
			content += '<div><img src="' + base_path + decodeURIComponent(gallery_content[id]['fails']) + '" align="left" style="margin-right:15px;"></div>';
			content += '<div style="overflow:hidden;"><b>' + gallery_t['Title'] + ': *</b><br />';
			content += '<input type="text" name="gallery_editnodes_dialog_description_'+ nid +'" id="gallery_editnodes_dialog_title_'+ nid +'" style="width:300px;" value="' + gallery_addslashes(decodeURIComponent(gallery_content[id]['title'])) + '"/><br />';
			content += '<b>' + gallery_t['Description'] + ':</b><br />';
			content += '<textarea name="gallery_editnodes_dialog_description_'+ nid +'" id="gallery_editnodes_dialog_description_'+ nid +'" rows="2" style="width:400px;">' + decodeURIComponent(gallery_content[id]['description']) + '</textarea></div>';
			
		content += '</div>';
	});
	
	content += '</div>';
						
	content += '<br />';
	content += '<input type="button" value="' + gallery_t['Save'] + '" onClick="gallery_editnodes_dialog_save()" style="float:right" />';
						
	$('.gallery_editnodes_dialog').html(content);
}

/* 
	function for saving changes (if any) on dialog
*/
function gallery_editnodes_dialog_save() {
	$('.gallery_selected').each( function (i) {
			
		var nid = this.id.split("-");
		nid = nid[1];
		var id = gallery_id_by_nid(gallery_content, nid);
		
		if (decodeURIComponent(gallery_content[id]['title']) != gallery_stripslashes($('#gallery_editnodes_dialog_title_'+ nid).val())) {
			gallery_content[id]['title'] = encodeURIComponent(gallery_stripslashes($('#gallery_editnodes_dialog_title_'+ nid).val()));
			$('#gallery_entry-'+nid).children(".gallery_entry_handler").html($('#gallery_editnodes_dialog_title_'+ nid).val());
			
			gallery_warnchanges(true);
			// can be ajax update
		}
		if (gallery_content[id]['description'] != undefined) {
			if (decodeURIComponent(gallery_content[id]['description']) != $('#gallery_editnodes_dialog_description_'+ nid).val()) {
				gallery_content[id]['description'] = encodeURIComponent($('#gallery_editnodes_dialog_description_'+ nid).val());
				
				gallery_warnchanges(true);
				// can ajax update
			}
		}
	});
	// update gallery
	$('.gallery_editnodes_dialog').jqmHide(); 
}

/*
	selects thumbnail
*/
function gallery_edit_thumb() {
	// if any field selected
	if ($('.gallery_selected').size() > 0) {
		// if the field wasnt selected as thumb already
		if ($('.gallery_selected').eq(0).children(".gallery_favpic").html() == '') {
			$('.gallery_entry').children(".gallery_favpic").html('');
			$('.gallery_selected').eq(0).children(".gallery_favpic").html('<img src="' + prog_gallery_base + 'images/button_icons/favorite.gif" align="top">');
			$('.gallery_selected').eq(0).each(function(i){ 
				id = this.id.split("-");
				id = id[1];
				galleries['thumb'] = encodeURIComponent(id);
			});
		
			gallery_warnchanges(true);
			
			// can be ajax update
		}
	} 
}

/*
	function for removing the field
*/
function gallery_edit_remove() {
	$('.gallery_selected').each( function (i) {
			
		nid = this.id.split("-");
		nid = nid[1];
		
		// if this was thumb image of gallery, set thumb to empty
		if ($(this).children(".gallery_favpic").html() != '') galleries['thumb'] = '';
		
		$(this).remove();
		id = gallery_id_by_nid(gallery_content, nid);
		delete gallery_content[id];
		
		gallery_warnchanges(true);
		// can ajax update
	});
}

/*
	shows/hides warning on content changes 
*/
function gallery_warnchanges(visible) {
	if (visible == true) {
		// if it wasnt expanded yet
		if ($('.gallery_warnchanges').height() != 21) {
			$('.gallery_warnchanges').attr("style","visibility:visible");
			$('.gallery_warnchanges').html(gallery_t['ClickUpdateToSaveChanges']);
			$('.gallery_warnchanges').animate({ height: '21', opacity: 'show'}, "normal", false);
			
			$("#gallery_editbuttons_update").removeAttr("disabled");
			$("#gallery_editbuttons_cancel").removeAttr("disabled");
			
			$("#gallery_editbuttons_autosort").attr("disabled","disabled");
		}
	} else {
		// if it wasnt hidden yet
		if ($('.gallery_warnchanges').height() != 0) {
			$('.gallery_warnchanges').animate({ height: '0', opacity: 'hide'}, "normal", function () {$('.gallery_warnchanges').attr("style","visibility:hidden");});
			
			$("#gallery_editbuttons_update").attr("disabled","disabled");
			$("#gallery_editbuttons_cancel").attr("disabled","disabled");
			
			$("#gallery_editbuttons_autosort").removeAttr("disabled");
		}
	}
}

/*
	return array's index by nid
*/
function gallery_id_by_nid(arr, val) {
	for (var i=0; i < arr.length; i++) {
		if (arr[i]) if (decodeURIComponent(arr[i]['nid']) == val) return i;
	}
	return false;
};

/*
	reload gallery data
*/
function gallery_edit_cancel() {
	gallery_list_destroysortable();
	gallery_list_load();					
	gallery_warnchanges(false);
}

/*
	save changes using ajax post request
*/ 

function gallery_edit_update() {

	$('.gallery_warnchanges').html('<img src="' + prog_gallery_base + 'images/loading.gif" />');
	
	
	$.post(base_path + prog_gallery_q + 'prog_gallery_ajax/save_album',
		{
			gallery_content: $.phpSerialize(gallery_content),
			gallery_list: $('.gallery_list').sortable( "serialize", { attribute: "id", key: "gallery_list" } ),
			gallery_thumb: galleries['thumb']
		}, 
		function (data, textStatus) {
			gallery_warnchanges(false);
		}
	);
	
	
}

function gallery_addimages_dialog_open() {
	gallery_selectfield($('.gallery_selected'));
	
	$('.gallery_addimages_dialog').jqmShow();
	
  if (prog_swfupload_found) {
    
    gallery_addimages_dialog();
    
    if (typeof(swfu) == 'undefined') {
      swfu = new SWFUpload(settings);
      // give 3 secs to load swfu object
      gallery_checkswfu_id = setTimeout("gallery_checkswfu()", 3000);
    }
  } else gallery_addimages_dialog_noswfu(); 
}

/*
  check if swfu object loaded
*/
function gallery_checkswfu(){
  if (typeof(swfu.getMovieElement().StartUpload) !== "function") {
    gallery_addimages_dialog_noswfu();
    swfupload_loaded = false;
  } else swfupload_loaded = true;
}

/* 
  stop timeout started to show noswfu mode
*/
function gallery_swfUploadLoaded(){
  clearTimeout(gallery_checkswfu_id);
  swfupload_loaded = true;
}
  
/*
	dialog for uploading images without swfu field
*/
function gallery_addimages_dialog_noswfu() {				
	var content = '<h2>' + gallery_t['upload_images'] + '</h2>';
	content += '<hr /><br />';
	content += '<form><input type="file" id="gallery_fileupload_' + gallery_noswfu_submitfile_id  + '" name="Filedata" / onChange="javascript:gallery_noswfu_submitfile(this)"></form>';
	
	content += '<fieldset class="flash" id="gallery_UploadProgress">';
	content += '<legend>' + gallery_t['upload_queue'] + '</legend>';
	content += '</fieldset>';
	content += '<div id="divStatus">0 ' + gallery_t['files_uploaded'] + '</div>';
	
	content += '<input type="button" value="' + gallery_t['Close'] + '" onClick="gallery_addimages_dialog_close();" style="float:right" />';
	
	// link to fallback
	content += '<div><a href="' + base_path + prog_gallery_q + 'nojs/newimage' + '"><small class="gallery_addimages_dialog_fallbacklink">' + gallery_t['open_nojs'] + '</small></a></div>';
		
	$('.gallery_addimages_dialog').html(content);
}

/* 
	noswfu file upload routine
*/
function gallery_noswfu_submitfile(elem) {
	content = '<div id="gallery_noswfu_queue-' + gallery_noswfu_submitfile_id + '" class="gallery_noswfu_queue gallery_noswfu_queue_waiting">';
	content += '<span class="gallery_noswfu_queue_filename">' + elem.value + '</span><br />';
	content += '<iframe id="gallery_uploadframe_' + gallery_noswfu_submitfile_id  +'" name="gallery_uploadframe_' + gallery_noswfu_submitfile_id  +'" class="gallery_noswfu_iframe" scrolling="no" marginwidth="0" marginheight="0"></iframe>';
	content += '<div class="gallery_noswfu_queue_loading"></div>'; 
	content += '<span class="gallery_noswfu_queue_status">' + gallery_t['waiting'] + '</span>';
	content += '</div>';
	$('#gallery_UploadProgress').prepend(content);
	
	// hide this upload form
	$(elem).hide();

	// increment file input field counter value
	gallery_noswfu_submitfile_id++;
	
	// add new upload field
	content = '<form><input type="file" id="gallery_fileupload_' + gallery_noswfu_submitfile_id  + '" name="Filedata" / onChange="javascript:gallery_noswfu_submitfile(this)"></form>';
	$(elem).parent().after(content);
}

/*
	queue check
*/
function gallery_addimages_noswfu_queuecheck() {
	if (gallery_noswfu_queue_currlen < 1) {
		if ($('.gallery_noswfu_queue_waiting').size() > 0) {
			
			// get last waiting file in queue
			var qid = $('.gallery_noswfu_queue_waiting:last').attr('id').split("-")[1];
			
			// submit file 
			$('#gallery_fileupload_' + qid).parent().attr('action',base_path + prog_gallery_q  + 'prog_gallery_ajax/upload')		;
			$('#gallery_fileupload_' + qid).parent().attr('enctype','multipart/form-data');
			$('#gallery_fileupload_' + qid).parent().attr('method','post');
			$('#gallery_fileupload_' + qid).parent().attr('target','gallery_uploadframe_' + qid);
			$('#gallery_fileupload_' + qid).parent().submit();
			
			// change status to uploading
			$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_status').html(gallery_t['uploading']);
			$('#gallery_noswfu_queue-' + qid).removeClass('gallery_noswfu_queue_waiting').addClass('gallery_noswfu_queue_uploading');
			
			// insert progressbar
			$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_loading').html('<img src="' + prog_gallery_base + 'images/loading2.gif" />');
			
			gallery_noswfu_queue_currlen++;
		}	
	} else {
		if ($('.gallery_noswfu_queue_uploading').size() > 0) {
			$('.gallery_noswfu_queue_uploading').each( function (i) {
				var qid = $(this).attr('id').split("-")[1];
				var status = $('#gallery_noswfu_queue-' + qid + ' > #gallery_uploadframe_' + qid).contents().children(":first").text(); 
			
				// check if file uploaded
				if (status != '') {
					if (status != 'fail'){
						// change status to done
						$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_status').html(gallery_t['done']);
						$('#gallery_noswfu_queue-' + qid).removeClass('gallery_noswfu_queue_uploading').addClass('gallery_noswfu_queue_done');
					
						// remove progressbar
						$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_loading').html('');
	
						// add to image list
						prog_gallery_upload_checkreturneddata(status);
						
						// update current queue status		
						gallery_noswfu_queue_currlen--;
						
						// update uploaded files field
						gallery_noswfu_uploaded++;
						$('.gallery_addimages_dialog > #divStatus').html(gallery_noswfu_uploaded + ' ' + gallery_t['files_uploaded']);
						
						// hide this field after some time
						setTimeout("gallery_noswfu_hidestatus("+ qid +")", 3000);
								
					} else {
						// remove progressbar
						$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_loading').html('');
	
						// change status to error
						$('#gallery_noswfu_queue-' + qid + ' > .gallery_noswfu_queue_status').html(gallery_t['error'] + ' :' + status.substring(0,10));
						$('#gallery_noswfu_queue-' + qid).removeClass('gallery_noswfu_queue_uploading').addClass('gallery_noswfu_queue_error');
						
						// hide this field after some time
						setTimeout("gallery_noswfu_hidestatus("+ qid +")", 3000);
					} 
				}
					
			});
		} else {
			// or else fieldset is now empty so we can just reset queue length to 0
			gallery_noswfu_queue_currlen = 0;
		}
	}
}

/*
	dialog for uploading images
*/
function gallery_addimages_dialog() {

	if ((($('.gallery_addimages_dialog').html()).length) == 0) {
	
		var content = '<h2>' + gallery_t['upload_images'] + '</h2>';
		content += '<hr />';
		// content += '<div id="content">';
			content += '<form id="form1" action="index.php" method="post" enctype="multipart/form-data">';
				
				// upload button placeholder
				content += '<div class="gallery_editbuttons"><span id="gallery_spanButtonPlaceHolder"></span></div>';
				
				content += '<fieldset class="flash" id="fsUploadProgress">';
				content += '<legend>' + gallery_t['upload_queue'] + '</legend>';
				content += '</fieldset>';
				content += '<div id="divStatus">0 ' + gallery_t['files_uploaded'] + '</div>';
			content += '</form>';
		// content += '</div>';
		content += '<br />';
		
		content += '<input type="button" value="' + gallery_t['Close'] + '" onClick="gallery_addimages_dialog_close();" style="float:right"  class="gallery_editbuttons" />';
		
		content += '<input id="btnCancel" type="button" value="' + gallery_t['CancelAllUploads'] + '" onclick="swfu.cancelQueue();" disabled="disabled" style="float:right;margin-right:3px;" class="gallery_editbuttons" />';
		
		// link to fallback
		content += '<small onClick="gallery_addimages_dialog_noswfu()" class="gallery_addimages_dialog_fallbacklink">' + gallery_t['open_noswfu'] + '</small>';
		
		$('.gallery_addimages_dialog').html(content);
	}
}

/*
	Close add image dialog
*/
function gallery_addimages_dialog_close() {

	//if (swfupload_loaded) 
	//	if ($('#btnCancel[@disabled=disabled]').size() == 0) swfu.cancelQueue(); 
	
	$('.gallery_addimages_dialog').jqmHide();
	
	if ($('.gallery_selected').size() > 0) {
		gallery_editnodes_dialog();
		$('.gallery_editnodes_dialog').jqmShow();
	}
}

/*
	checking server response when file is uploaded
*/
function prog_gallery_upload_checkreturneddata(serverData) {
	if (serverData != 'fail') {
		
		// load now node's data from server
		var i = gallery_content.length;
		var gallery_content_entry = $.phpUnserialize(serverData);
		gallery_content[i] = gallery_content_entry;

		// if no thumb set for gallery, then set it to this one
		if (galleries['thumb'] == '0') galleries['thumb'] = gallery_content[i]['nid'];

		// adding a field. 
		gallery_list_destroysortable()
		gallery_list_addentry(i);
		gallery_list_makesortable();
		
		// make field selected, so you can load them in editor afterwards
		gallery_selectfield($('#gallery_entry-' + gallery_content[i]['nid']))
		
 	} 
}

/*
	Close image upload status message 
*/
function gallery_noswfu_hidestatus(qid) {
	$('#gallery_noswfu_queue-' + qid).fadeOut("slow", function () {
		$(this).remove();
	});
}  
 
/*
	sort images by exif
*/ 

function gallery_edit_autosort() {
	$('.gallery_list').html('<img src="' + prog_gallery_base + 'images/loading.gif" />');
	
	// send command to sort images	
	$.post(base_path + prog_gallery_q + 'prog_gallery_ajax/autosort_album',{}, 
		function (data, textStatus) {
			// reload data
			gallery_list_destroysortable();
			gallery_list_load();	
		}
	);
} 
 
/* 
	equivalent of php addslashes / stripslashes (found on net)
*/
function gallery_addslashes(str) {
	str=str.replace(/\\/g,'\\\\');
	str=str.replace(/\0/g,'\\0');
  str=str.replace(/\'/g,'\\\'');
	str=str.replace(/\"/g,'\\"');
	return str;
}
function gallery_stripslashes(str) {
	str=str.replace(/\\\\/g,'\\');
	str=str.replace(/\\'/g,'\'');
	str=str.replace(/\\"/g,'"');
	str=str.replace(/\\0/g,'\0');
	return str;
}
