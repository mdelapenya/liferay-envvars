var list = document.querySelector('.list');


// Insert fetch data method below
WeDeploy
	.data('data-envvars.wedeploy.io')
	.orderBy('id', 'desc')
	.limit(5)
	.get('encodes')
	.then(function(response) {
		appendEncodes(response);
	})
	.catch(function(error) {
		console.error(error);
	});
// Insert fetch data method above

function appendEncodes(encodes) {
	var encodesList = '';

	encodes.forEach(function(encode) {
		encodesList += `<input type="text" value="${encode.name}" readonly>`;
	});

	list.innerHTML = encodesList;
}