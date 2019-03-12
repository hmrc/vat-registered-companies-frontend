var showHide = function () {
    var showHideContent = new GOVUK.ShowHideContent()
    showHideContent.init()
}
if (document.addEventListener) {
    document.addEventListener('DOMContentLoaded', function () {
        showHide()
    })
} else {
    window.attachEvent('onload', function () {
        showHide()
    })
}

window.onload = function() {

    if(document.getElementById('get-help-action')) {
        document.getElementById('get-help-action').addEventListener('click', function() {
            ga('send', 'event', 'link-click', document.title, 'Get help with this page');
        });
    }

}

