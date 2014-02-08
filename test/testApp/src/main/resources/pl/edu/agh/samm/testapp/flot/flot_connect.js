window.pl_edu_agh_samm_testapp_flot_FlotChart = function () {

    var element = $(this.getElement());
    var plot = null;

    this.onStateChange = function () {
        if (plot == null) {
            $.plot(
                element,
                [
                    {
                        data: this.getState().values
                    }
                ],
                {
                    colors: [this.getState().color]
                }
            );
        } else {
            plot.setData(this.getState().values);
        }
    }

}