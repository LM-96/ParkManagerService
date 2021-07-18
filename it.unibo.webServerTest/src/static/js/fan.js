
var slider_fan = document.getElementById("fan");
var output_fan = document.getElementById("value_fan");
output_fan.innerHTML = slider_fan.value;

slider_fan.onchange = function() {
    output_fan.innerHTML = this.value;
    fan_socket.send(JSON.stringify({
        'data': this.value,
    }));
};


const fan_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/ws/fan/'
);

fan_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    document.querySelector('#msg_fan').innerHTML = (data.data)

}