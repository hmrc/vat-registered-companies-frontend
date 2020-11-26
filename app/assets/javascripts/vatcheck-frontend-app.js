
window.onload = function() {

    var helpLinks = document.getElementsByClassName('get-help-action');
    for (let i=0; i<helpLinks.length; i++) {
        helpLinks.item(i).addEventListener('click', function() {
            ga('send', 'event', 'link-click', document.title, 'Get help with this page');
        });
    }

    if(document.getElementById('exit-survey')) {
        document.getElementById('exit-survey').addEventListener('click', function() {
            ga('send', 'event', 'link-click', document.title, 'What did you think of this service');
        });
    }

}

