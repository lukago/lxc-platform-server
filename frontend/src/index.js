import ReactDOM from 'react-dom';
import React from 'react';
import {Provider} from 'react-redux';
import initStore from 'config/store';
import DevTools from 'config/devtools';
import {registerLocales} from 'config/translation';
import {App} from "./components/App";

const devTools = process.env.NODE_ENV === 'development' ? <DevTools/> : null;
const store = initStore();
registerLocales(store);

ReactDOM.render(
    <Provider store={store}>
      <div>
        {devTools}
        <App />
      </div>
    </Provider>,
    document.getElementById('root')
);
