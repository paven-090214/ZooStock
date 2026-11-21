const { chromium } = require(process.env.ZOOSTOCK_PLAYWRIGHT_PATH || 'playwright')
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const os = require('node:os')
let qaBrowser

;(async () => {
  const browser = await chromium.launch({ channel: process.env.ZOOSTOCK_BROWSER_CHANNEL || (process.platform === 'win32' ? 'msedge' : undefined), headless: true })
  qaBrowser = browser
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
  const page = await context.newPage()
  await page.clock.install()
  const errors = []
  page.on('pageerror', (error) => errors.push(error.message))
  const tokens = ['Lion', 'Tiger', 'Leopard', 'Jaguar', 'Cheetah', 'Gray Wolf'].map((name, i) => ({ tokenId: i + 1, tokenName: name, tokenPrice: 9000 + i * 400, imgPath: '' }))
  const holdings = [{ holdingId: 1, memberUid: 1, tokenId: 1, quantity: 12, buyPriceAvg: 8200 }]
  let overviewRequests = 0, orders = 0, orderMode = 'conflict', protectedHeaders = [], force401 = false, chartMode = 'normal', marketFailure = false
  const requests = []
  await page.route('**/api/**', async (route) => {
    const request = route.request(), url = new URL(request.url()), endpoint = url.pathname.replace('/api', '')
    requests.push(endpoint + url.search)
    const fulfill = (data, status = 200) => route.fulfill({ status, contentType: typeof data === 'string' ? 'text/plain' : 'application/json', body: typeof data === 'string' ? data : JSON.stringify(data) })
    if (endpoint === '/login') {
      const payload = Buffer.from(JSON.stringify({ sub: '1', exp: Math.floor(Date.now() / 1000) + 3600 })).toString('base64url')
      return fulfill(`eyJhbGciOiJIUzI1NiJ9.${payload}.test-signature`)
    }
    if (endpoint === '/signup') return fulfill('회원가입 성공')
    if (endpoint === '/token') {
      overviewRequests++
      if (marketFailure) return fulfill({ message: '테스트 갱신 실패' }, 503)
      const ranking = (token, index) => ({ ...token, currentPrice: token.tokenPrice, startPrice: 8500, changeRate: index ? -2.6 : 7.2 })
      return fulfill({ randomTokens: overviewRequests > 1 ? tokens.slice(1) : tokens.slice(0, 5), dailyRankingResponse: { gainers: tokens.slice(0, 5).map(ranking), losers: tokens.slice(1).map(ranking) } })
    }
    if (endpoint === '/token/all') {
      const sort = url.searchParams.get('sort') || 'nameAsc'
      const result = [...tokens].sort((a, b) => sort.startsWith('price') ? a.tokenPrice - b.tokenPrice : a.tokenName.localeCompare(b.tokenName))
      if (sort.endsWith('Desc')) result.reverse()
      return fulfill(result)
    }
    if (endpoint === '/token/search') return fulfill(tokens.filter((token) => token.tokenName.includes(url.searchParams.get('name'))))
    if (/^\/token\/search\/\d+$/.test(endpoint)) {
      const token = tokens.find((item) => item.tokenId === Number(endpoint.split('/').at(-1)))
      return fulfill(token || { message: '조회할 수 있는 토큰이 없습니다.' }, token ? 200 : 404)
    }
    if (/^\/token\/\d+$/.test(endpoint)) return fulfill(Array.from({ length: chartMode === 'empty' ? 0 : chartMode === 'single' ? 1 : 20 }, (_, i) => ({ historyId: i + 1, tokenId: 1, price: 8900 + Math.sin(i / 2) * 250 + i * 8, recordedAt: `2026-11-21T12:${String(i).padStart(2, '0')}:00` })))
    if (endpoint.startsWith('/mypage') || endpoint.startsWith('/trade/')) {
      protectedHeaders.push(request.headers().authorization)
      if (force401) return fulfill({ message: 'JWT인증에 실패했습니다.' }, 401)
    }
    if (endpoint === '/mypage') return fulfill(holdings)
    if (endpoint === '/mypage/history') return fulfill([{ tradeId: 1, memberUid: 1, tokenId: 1, quantity: 12, tradePrice: 8200, tradeType: 'BUY', tradedAt: '2026-11-21T03:00:00Z' }])
    if (endpoint.startsWith('/trade/')) {
      orders++
      await new Promise((resolve) => setTimeout(resolve, 250))
      return orderMode === 'conflict' ? fulfill('다른 거래가 먼저 처리되었습니다. 다시 시도해주세요.', 409) : fulfill('매수에 성공했습니다.')
    }
    return fulfill({ message: 'not found' }, 404)
  })
  const output = process.env.ZOOSTOCK_QA_OUTPUT || path.join(os.tmpdir(), 'zoostock-browser-qa'); fs.mkdirSync(output, { recursive: true })
  const visible = async (locator) => { await locator.waitFor({ state: 'visible', timeout: 10000 }) }
  const goto = async (url) => { await page.goto((process.env.ZOOSTOCK_QA_URL || 'http://127.0.0.1:5173') + url); await page.waitForTimeout(120) }
  await goto('/')
  await visible(page.getByRole('heading', { name: /새로운 투자의 세계/ }))
  await page.screenshot({ path: path.join(output, 'home-desktop.png'), fullPage: true })
  await goto('/token')
  await visible(page.locator('.discovery-card').first())
  assert.equal(await page.locator('.discovery-card').count(), 5)
  const firstIds = await page.locator('.discovery-card').evaluateAll((links) => links.map((link) => link.getAttribute('href')))
  const beforePoll = overviewRequests
  await page.clock.fastForward(31000)
  await page.waitForTimeout(250)
  assert(overviewRequests > beforePoll, '30 second automatic polling')
  await page.getByRole('button', { name: '지금 새로고침' }).click()
  await page.waitForTimeout(200)
  assert(overviewRequests > 1)
  assert.deepEqual(await page.locator('.discovery-card').evaluateAll((links) => links.map((link) => link.getAttribute('href'))), firstIds)
  marketFailure = true
  await page.getByRole('button', { name: '지금 새로고침' }).click()
  await visible(page.getByText('갱신 실패 · 마지막 데이터 표시 중', { exact: false }))
  assert.equal(await page.locator('.discovery-card').count(), 5)
  marketFailure = false
  await page.getByRole('button', { name: '지금 새로고침' }).click()
  await page.waitForTimeout(200)
  await page.screenshot({ path: path.join(output, 'market-desktop.png'), fullPage: true })
  await goto('/token/all')
  await visible(page.getByRole('combobox'))
  for (const [sort, firstName] of [['nameAsc', 'Cheetah'], ['nameDesc', 'Tiger'], ['priceAsc', 'Lion'], ['priceDesc', 'Gray Wolf']]) {
    await page.getByRole('combobox').selectOption(sort)
    await page.waitForTimeout(200)
    assert.match(await page.locator('tbody tr').first().innerText(), new RegExp(firstName))
  }
  assert.match(page.url(), /sort=priceDesc/)
  assert(requests.includes('/token/all?sort=priceDesc'))
  assert.match(await page.locator('tbody tr').first().innerText(), /Gray Wolf/)
  await page.getByRole('textbox', { name: '토큰 이름 검색' }).fill('NO_RESULT')
  await page.getByRole('button', { name: '검색', exact: true }).click()
  await visible(page.getByText('검색 결과 또는 등록된 토큰이 없습니다.'))
  await goto('/token/1')
  await visible(page.getByRole('heading', { name: 'Lion', exact: true }))
  await visible(page.getByRole('img', { name: /가격 이력/ }))
  await page.getByRole('button', { name: '5시간', exact: true }).click()
  await visible(page.getByRole('img', { name: /가격 이력/ }))
  assert(requests.includes('/token/1?history=5h'))
  await page.getByRole('link', { name: '로그인하고 거래하기' }).click()
  await visible(page.getByRole('heading', { name: '로그인', exact: true }))
  await page.getByLabel('아이디', { exact: true }).fill('test')
  await page.getByLabel('비밀번호', { exact: true }).fill('test-password')
  await page.getByRole('button', { name: /로그인/ }).click()
  await visible(page.getByRole('heading', { name: 'Lion', exact: true }))
  await visible(page.getByRole('button', { name: '매수 주문 확인', exact: true }))
  await page.getByRole('button', { name: '매수 주문 확인', exact: true }).click()
  await page.getByRole('button', { name: '1일', exact: true }).click()
  await visible(page.getByRole('button', { name: '매수 확정', exact: true }))
  await page.getByRole('button', { name: '매수 확정', exact: true }).click()
  assert.equal(await page.getByRole('button', { name: '거래 처리 중…' }).isDisabled(), true)
  await visible(page.getByText('다른 거래가 먼저 처리되었습니다. 다시 시도해주세요.', { exact: true }))
  assert.equal(orders, 1, '409 must not retry automatically')
  orderMode = 'success'
  await page.getByRole('button', { name: '매수 주문 확인', exact: true }).click()
  await page.getByRole('button', { name: '매수 확정', exact: true }).click()
  await visible(page.getByText('매수에 성공했습니다.', { exact: true }))
  assert.equal(orders, 2)
  assert(protectedHeaders.every((value) => value?.startsWith('Bearer ')))
  await page.reload()
  await visible(page.getByRole('button', { name: '매수 주문 확인', exact: true }))
  await page.screenshot({ path: path.join(output, 'detail-desktop.png'), fullPage: true })
  await goto('/mypage')
  await visible(page.getByRole('cell', { name: '12개' }))
  await page.getByRole('link', { name: /거래 기록 보기/ }).click()
  await visible(page.getByRole('cell', { name: '매수', exact: true }))
  assert.match(await page.locator('tbody').innerText(), /Lion/)
  force401 = true
  await page.getByRole('button', { name: '지금 새로고침' }).click()
  await visible(page.getByRole('heading', { name: '로그인', exact: true }))
  assert.equal(await page.evaluate(() => sessionStorage.getItem('zoostock.accessToken')), null)
  force401 = false
  await page.evaluate(() => sessionStorage.setItem('zoostock.accessToken', 'header.' + btoa(JSON.stringify({ sub: '1', exp: Math.floor(Date.now() / 1000) + 2 })) + '.signature'))
  await goto('/mypage')
  await page.clock.fastForward(3000)
  await visible(page.getByRole('heading', { name: '로그인', exact: true }))
  assert.equal(await page.evaluate(() => sessionStorage.getItem('zoostock.accessToken')), null)
  await goto('/signup')
  await page.getByLabel('닉네임', { exact: true }).fill('테스트')
  await page.getByLabel('아이디', { exact: true }).fill('new-user')
  await page.getByLabel('비밀번호', { exact: true }).fill('pass')
  await page.getByLabel('비밀번호 확인', { exact: true }).fill('other')
  await page.getByRole('button', { name: /회원가입/ }).click()
  await visible(page.getByText('비밀번호 확인이 일치하지 않습니다.'))
  await page.getByLabel('비밀번호 확인', { exact: true }).fill('pass')
  await page.getByRole('button', { name: /회원가입/ }).click()
  await visible(page.getByText('회원가입이 완료되었습니다. 로그인해주세요.'))
  await goto('/token/999')
  await visible(page.getByText('조회할 수 있는 토큰이 없습니다.'))
  chartMode = 'empty'
  await goto('/token/1')
  await visible(page.getByText('아직 이 기간에 기록된 가격이 없어요.'))
  chartMode = 'single'
  await page.getByRole('button', { name: '5시간', exact: true }).click()
  await visible(page.getByRole('img', { name: /가격 이력/ }))
  chartMode = 'normal'
  await page.setViewportSize({ width: 390, height: 844 })
  for (const [url, name] of [['/', 'home-mobile'], ['/token', 'market-mobile'], ['/token/all', 'list-mobile'], ['/token/1', 'detail-mobile'], ['/login', 'login-mobile']]) {
    await goto(url)
    await page.waitForTimeout(200)
    assert(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), `${url} overflows viewport`)
    await page.screenshot({ path: path.join(output, name + '.png'), fullPage: true })
  }
  await page.setViewportSize({ width: 320, height: 740 })
  await goto('/')
  assert(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), '320px layout')
  assert.deepEqual(errors, [], 'No browser runtime errors')
  console.log(JSON.stringify({ result: 'PASS (mocked API, no real DB trades)', scenarios: ['desktop/mobile layout including 320px', 'sorting', 'search empty', '30-second polling', 'stable random selection', 'refresh failure preserves data', 'chart periods', 'empty/single-point chart', 'login redirect', 'signup validation', 'session reload', 'bearer header', '409 no retry', 'pending order button disabled', 'successful order', 'portfolio/history', '401 clears session', 'timer expiry', 'unknown token'], orders, overviewRequests, errors, screenshots: output }, null, 2))
  await browser.close()
})().catch(async (error) => { console.error(error); if (qaBrowser) await qaBrowser.close(); process.exitCode = 1 })
