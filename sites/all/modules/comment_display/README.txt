/* $Id: README.txt,v 1.1 2008/10/26 15:20:04 sun Exp $ */

-- SUMMARY --

Comment Display prevents rendering of comments directly appended to the node
itself.  Instead it plugs them into a $comments variable that can be output
anywhere in a theme.

For a full description visit the project page:
  http://drupal.org/project/comment_display
Bug reports, feature suggestions and latest developments:
  http://drupal.org/project/issues/comment_display


-- REQUIREMENTS --

* Comment module in Drupal core or alternative implementations.


-- INSTALLATION --

* Install as usual, see http://drupal.org/node/70151 for further information.


-- CONFIGURATION --

* At the time of this writing, this module provides no configuration or any
  other administrative elements.


-- USAGE --

* Insert the following snippet into your page.tpl.php:

  <?php print $comments; ?>


-- CONTACT --

Current maintainers:
* Daniel F. Kudwien (sun) - dev@unleashedmind.com

This project has been sponsored by:
* UNLEASHED MIND
  Specialized in consulting and planning of Drupal powered sites, UNLEASHED
  MIND offers installation, development, theming, customization, and hosting
  to get you started. Visit http://www.unleashedmind.com for more information.

