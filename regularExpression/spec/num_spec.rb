RSpec.describe 'Regular Expression for C++ integer literals' do
  # Define parts of the regular expression for different types of literals

  let(:nonzero_decimal_digit) { /[1-9]/ }
  let(:decimal_digit) { /[0-9]/ }
  let(:octal_digit) { /[0-7]/ }
  let(:hex_digit) { /[0-9a-fA-F]/ }
  let(:binary_digit) { /[01]/ }

  # Define regex patterns for each type of integer literal
  let(:decimal) { /-?(#{nonzero_decimal_digit}#{decimal_digit}*('?[0-9]{3})*|0)/ }
  let(:octal) { /0#{octal_digit}+('?[0-7]{3})*/ }
  let(:hexadecimal) { /0[xX]#{hex_digit}+('?[0-9a-fA-F]{2})*/ }
  let(:binary) { /0[bB]#{binary_digit}+('?[01]{4})*/ }

  # Define suffix pattern (optional u, U, l, L, ll, or LL)
  let(:suffix) { /[uU]?[lL]{0,2}/ }

  # Complete pattern combining all types of literals with suffix
  let(:pattern) { /\A(#{decimal}|#{octal}|#{hexadecimal}|#{binary})#{suffix}\z/ }

  # Test cases for literals that should pass
  let(:should_pass) do
    [
      "1", "-33'000", "4525235", "10'080", "123'456'789", "1ul", "1u", "1ll", "0", "0755", "0x1A3F", "0b1010",
      "0xFFu", "0777ul", "0b111LL", "123U", "456l", "789LL"
    ]
  end

  # Test cases for literals that should fail
  let(:should_fail) do
    [
      "01189", "0xGHI", "0b102", "123abc", "ul", "LL", "3ulul", "1.23", "-0x1F", "++33", "0b", "0x", "12.3", "u1",
      "0LLL", "0b2"
    ]
  end

  # Tests for literals that should pass
  describe 'should pass' do
    it 'matches all expected valid literals' do
      should_pass.each do |str|
        expect(str).to match(pattern)
      end
    end
  end

  # Tests for literals that should fail
  describe 'should fail' do
    it 'does not match any invalid literals' do
      should_fail.each do |str|
        expect(str).not_to match(pattern)
      end
    end
  end
end
