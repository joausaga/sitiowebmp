if (Drupal.jsEnabled) {
  $(document).ready(function() {
    load_twitter_button = function() {

      if (load_twitter_button.scriptadded) {
        twttr.widgets.load();
      } else {
        $('a.service-links-twitter-widget').each(function(){
          $(this).attr('href', $(this).attr('href').replace(/((?:counturl\=|^))http[s]*\%3A\/\//g, "$1"));
        });
        $.getScript('http://platform.twitter.com/widgets.js', function () {
          load_twitter_button.scriptadded = true;
        });
      }
    }

    load_twitter_button.scriptadded = false;
    load_twitter_button();
  });
}
