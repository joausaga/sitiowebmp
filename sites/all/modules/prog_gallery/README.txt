/* $Id */
-- SUMMARY --

AJAX based galleries module is (partially) based on mockup seen on web - http://www.raincitystudios.com/blogs-and-pods/hubert/outline-ui-design-ajax-image-gallery

Summarizing pros/cons for image/imagefield I decided to go with image nodes. Also didn't use views/taxonomy, because node has no weight field, so I had to make my own structure.

This module uses a lot of jquery (and contributed jquery plugins) so update it properly. UI simplicity is kept in mind while making it.

Multiple file upload done using SWFUpload from http://www.swfupload.org


-- REQUIREMENTS --

image


-- INSTALLATION --

* Install as usual, see http://drupal.org/node/70151 for further information.


-- CONFIGURATION --

Module relies on image node derivatives 'thumbnail' and 'preview'. You can set up these on "admin/settings/image", "Image sizes" section.

No module specific configuration.

-- CONTACT --

Current maintainers:
* Janis Bebritis (Jancis) - jancis@iists.it

-- SPECIAL THANKS --

Big thanks for helpfull hand and inspiration goes to Mikelis Zalais (mike-green). Also thanks for testing and stuff, you're in my README file now ;)
