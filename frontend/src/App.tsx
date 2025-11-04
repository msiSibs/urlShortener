import './App.css'

function App() {
  return (
    <div className="app-container">
      <div className="main-content">
        <div className="text-center">
          <h1 className="title">
            URL Shortener
          </h1>
          <p className="subtitle">
            Transform long URLs into short, shareable links
          </p>
        </div>
        
        <div className="card">
          <div className="form-group">
            <label htmlFor="url-input" className="label">
              Enter your long URL
            </label>
            <input
              id="url-input"
              type="url"
              placeholder="https://example.com/very/long/url"
              className="input"
            />
          </div>
          <button type="button" className="button">
            Shorten URL
          </button>
          
          <div className="result-area">
            <p className="result-text">
              Your shortened URL will appear here
            </p>
          </div>
        </div>
        
        <div className="footer">
          <p className="footer-text">
            Built with Spring Boot and React
          </p>
        </div>
      </div>
    </div>
  )
}

export default App
