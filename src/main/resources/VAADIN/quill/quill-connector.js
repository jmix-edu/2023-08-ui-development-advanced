com_company_demo_QuillTextEditor = function() {
    var connector = this;

    var element = connector.getElement();
    element.innerHTML = "<div id=\"editor\"></div>"


    connector.onStateChange = function() {
        var state = connector.getState();
        var options = state.data;

        var quill = new Quill('#editor', options);

        quill.on('text-change', function(delta, oldDelta, source) {
            if (source === 'user') {
              connector.valueChange(quill.getText());
            }
        });

        connector.deleteText = function() {
            quill.deleteText(0, quill.getLength());
        }
    }
};