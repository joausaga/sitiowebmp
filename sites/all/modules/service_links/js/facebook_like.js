if (Drupal.jsEnabled) {
  $(document).ready(function(){
    $('a.service-links-facebook-like').each(function(){
      var iframe = document.createElement('iframe');

      iframe.src = $(this).attr('href').replace('http://', '//').replace(/http[s]*\%3A\/\//, '');
      iframe.setAttribute('scrolling', 'no');
      iframe.setAttribute('frameborder', 0);
      iframe.setAttribute('allowTransparency', 'true');

      $(iframe).css({
        'border': 'none',
        'overflow': 'hidden',
        'width': Drupal.settings.ws_fl.width + 'px',
        'height': Drupal.settings.ws_fl.height + 'px',
      });

      $(iframe).addClass($(this).attr('class'));

      $(this).replaceWith(iframe); 
    });
  });
}
