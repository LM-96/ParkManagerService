
var slider_weight = document.getElementById("weight");
var output_weight = document.getElementById("value_weight");
output_weight.innerHTML = slider_weight.value;

slider_weight.onchange = function() {
    output_weight.innerHTML = this.value;
    weight_socket.send(JSON.stringify({
        'data': this.value,
    }));
};


const weight_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/ws/weightsensor/'
);

weight_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    document.querySelector('#msg_weight').innerHTML = (data.data)

}

weight_socket.onopen = function () {
    document.querySelector('#msg_weight').innerHTML = (slider_weight.value)
    weight_socket.send(JSON.stringify({
        'data': slider_weight.value,
    }));

}