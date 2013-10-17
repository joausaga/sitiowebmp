/* 
	onload function
*/
jQuery(document).ready(function() {
	//carousel is timeout`inf if enabled :(
  //var hash = window.location.hash;
  //if (hash.substring(1)) prog_gallery_carousel_start = hash.substring(1); 
  
	jQuery('#image_carousel').jcarousel({
		wrap: 'circular',
		itemVisibleInCallback: {onBeforeAnimation: prog_gallery_carousel_itemVisibleInCallback},
		itemVisibleOutCallback: {onAfterAnimation: prog_gallery_carousel_itemVisibleOutCallback},
		start: prog_gallery_carousel_start + prog_gallery_carousel_itemList.length,
		scroll: 5,
		visible: 5,
		buttonNextHTML: null,
    buttonPrevHTML: null
	});
	prog_gallery_carousel_loadImage(prog_gallery_carousel_start);
});

/* 
	callback functions
*/
function prog_gallery_carousel_itemVisibleInCallback(carousel, item, i, state, evt)
{
	prog_gallery_imagecarousel = carousel;

	// The index() method calculates the index from a
	// given index who is out of the actual item range.
	var idx = carousel.index(i, prog_gallery_carousel_itemList.length);
	carousel.add(i, prog_gallery_carousel_getItemHTML(prog_gallery_carousel_itemList[idx - 1]));
};

/* 
	callback functions
*/
function prog_gallery_carousel_itemVisibleOutCallback(carousel, item, i, state, evt)
{
    carousel.remove(i);
};

/**
	Item html creation helper.
 */
function prog_gallery_carousel_getItemHTML(item)
{
	return '<div class="gallery_album_thumb_container"><a href="#' + prog_gallery_carousel_itemList[item.index].nid + '"><img src="' + item.url + '" alt="' + item.title + '" id="prog_gallery_carousel_index-' + item.index + '" onClick="prog_gallery_carousel_start = ' + item.index + ';prog_gallery_carousel_loadImage(jQuery.jcarousel.intval(' + item.index + '))" /></a></div>';
};

/* 
	In a case given index is out of bounds, we normalize it
*/
function prog_gallery_carousel_getnormalindex(index) {
	index = jQuery.jcarousel.intval(index);
	if (index < 0) index = 0;	
	while (index >= prog_gallery_carousel_itemList.length) {
		index = index - prog_gallery_carousel_itemList.length;
	}	
	return index;
}

/* 
	callback functions
*/
function prog_gallery_carousel_loadImage(index) {
	
	index = prog_gallery_carousel_getnormalindex(index);
	
	// scroll carusel so that selected item is in center
	prog_gallery_imagecarousel.scroll(jQuery.jcarousel.intval(index) - 1);

	// load selected image 
	$('.gallery_album_image').attr("src", prog_gallery_base + 'images/loading.gif');
	$('.gallery_album_image').attr("src", prog_gallery_carousel_itemList[index].previewurl);
	$('.gallery_album_image').attr("alt", decodeURIComponent(prog_gallery_carousel_itemList[index].title));
	
	// set # address to link (for history handling)
	$('a.gallery_album_image_href').attr("href", "#" + prog_gallery_carousel_itemList[prog_gallery_carousel_getnormalindex(index)].nid);

	// load image description
	$('.gallery_album_image_description').html(decodeURIComponent(prog_gallery_carousel_itemList[prog_gallery_carousel_getnormalindex(index)].description));

	// load comments for this image node
	prog_gallery_ajaxcomments_getcomments(prog_gallery_carousel_itemList[index].nid);
	
	// modify "add comment" link-------
	$("li.comment_add > a").attr('href',	'#' + prog_gallery_carousel_itemList[index].nid);
	$("li.comment_add > a").click(function() {
			prog_gallery_ajaxcomments_form(prog_gallery_carousel_itemList[index].nid, "new",0,0);
			return false; 
	});
	// or we just remove "add comment" text from viewing the gallery
	//$("li.comment_add").html('');	
	
	// remove default comment add form (also clear comment form div)
	$("div.box").html('');
	
	return false;
}

