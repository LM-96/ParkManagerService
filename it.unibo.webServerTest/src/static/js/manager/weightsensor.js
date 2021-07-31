var status_indoor = document.getElementById("status_weightsensor");

const weight_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/weightsensor/'
);

weight_socket.onmessage = function (e) {
    console.log(e)
    const json_data = JSON.parse(e.data);
    console.log(json_data)
    status_indoor.innerHTML = json_data;
}
