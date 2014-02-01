window.pl_edu_agh_samm_testapp_flot_FlotChart = function () {

    var element = $(this.getElement());

    this.onStateChange = function () {
//        alert(this.getState().values)
        // TODO: instead of plotting it again - just use setData
        $.plot(
            element,
            [
                {
                    data: this.getState().values
                }
            ]
        );
    }

}