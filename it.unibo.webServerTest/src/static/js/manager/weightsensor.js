var status_indoor = document.getElementById("status_indoor");

const weight_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/weightsensor/'
);

weight_socket.onmessage = function (e) {
    const json_data = JSON.parse(e.data);
    console.log(json_data.data)
    const data = json_data.data
    status_indoor.innerHTML = data;
}
